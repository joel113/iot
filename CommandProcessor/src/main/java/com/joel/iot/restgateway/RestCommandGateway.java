package com.joel.iot.restgateway;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.joel.iot.commands.Command;
import com.joel.iot.commands.RestCommands;
import com.joel.iot.commands.RestGetCommand;
import com.joel.iot.commands.RestPutCommand;
import com.joel.iot.mqtt.MqttPublisher;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class RestCommandGateway {

	private String broker;
	private static Logger log = Logger.getRootLogger();

	public RestCommandGateway(String broker) {
		this.broker = broker;
		log.setLevel(Level.TRACE);
	}

	public void processCommands(String clientId, String topic) {

		MqttPublisher mqttsubject = new MqttPublisher(broker, clientId, topic);
		Observable<String> observable = mqttsubject.observe();
		observable.doOnSubscribe(new Action0() {
			@Override
			public void call() {
				log.info(String.format("Subscribe to %s to process events.", topic));
			}
		});
		observable.doOnUnsubscribe(new Action0() {
			@Override
			public void call() {
				log.info(String.format("Unsubscribe from %s.", topic));
			}
		});
		Observable<Command> observableCommand = observable.map(new Func1<String, Command>() {
			@Override
			public Command call(String json) {
				Gson gson = new Gson();
				return gson.fromJson(json, Command.class);
			}
		});
		observableCommand.doOnNext(new Action1<Command>() {
			@Override
			public void call(Command command) {
				log.trace(String.format("Processing command with subject %s.", command.getCommand()));
			}
		});
		
		try {
			// Parse the RestGetCommandObservers from the JSON document to setup the objects
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("commands.json").getFile());
			JsonReader reader = new JsonReader(new FileReader(file));
			RestCommands commands = new Gson().fromJson(reader, RestCommands.class);
			List<RestGetCommand> restGetCommands = commands.getRestgetcommands();
			List<RestPutCommand> restPutCommands = commands.getRestputcommands();
			
			// Setup the filtering based on the RestGetCommandObservers from the JSON document
			for(RestGetCommand restGetCommand : restGetCommands) {
				// Filter the commands
				Observable<Command> observableFilteredCommand = observableCommand.filter(new Func1<Command,Boolean>() {
					@Override
					public Boolean call(Command command) {
						if(command.getCommand().equals(restGetCommand.getCommand())) {
							return true;
						}
						else {
							return false;
						}
					}
				});
				// Replace the command with a restgetcommand
				Observable<RestGetCommand> observableRestGetFilteredCommand = observableFilteredCommand.map(new Func1<Command, RestGetCommand>() {
					@Override
					public RestGetCommand call(Command t) {
						return restGetCommand;
					}
				});
				observableRestGetFilteredCommand.doOnNext(new RestGetCommandAction<RestGetCommand>());
			}
			
			// Setup the filtering based on the RestPutCommandObservers from the JSON document
			for(RestPutCommand restPutCommand : restPutCommands) {
				// Filter the commands
				Observable<Command> observableFilteredCommand = observableCommand.filter(new Func1<Command,Boolean>() {
					@Override
					public Boolean call(Command command) {
						if(command.getCommand().equals(restPutCommand.getCommand())) {
							return true;
						}
						else {
							return false;
						}
					}
				});
				// Replace the command with a restputcommand
				Observable<RestPutCommand> observableRestPutFilteredCommand = observableFilteredCommand.map(new Func1<Command, RestPutCommand>() {
					@Override
					public RestPutCommand call(Command t) {
						return restPutCommand;
					}
				});
				observableRestPutFilteredCommand.doOnNext(new RestPutCommandAction<RestPutCommand>());
			}
			
		}
		catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}	
			
	}

    public static void main(String[] args) {
    	try {
	        SimpleLayout layout = new SimpleLayout();
	        ConsoleAppender consoleAppender = new ConsoleAppender( layout );
	        log.addAppender(consoleAppender);
	        FileAppender fileAppender = new FileAppender( layout, "logs/restgateway.log", false );
	        log.addAppender(fileAppender);
    	}
    	catch(Exception ex) {
    		System.out.println(ex);
    	}

    	RestCommandGateway processor = new RestCommandGateway("tcp://joel-flocke:1883");
    	processor.processCommands("rest-gateway-coppola-commands","/joel-coppola/commands");
    	processor.processCommands("rest-gateway-flocke-commands","/joel-flocke/commands");
    	processor.processCommands("rest-gateway-smudo-commands","/joel-smudo/commands");
    }
}
