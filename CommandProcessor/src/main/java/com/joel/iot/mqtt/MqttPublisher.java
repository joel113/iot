package com.joel.iot.mqtt;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MqttPublisher {
	
	private String broker;
	private String clientId;
	private String topic;
	private MqttClient client;
	private final PublishSubject<String> subject = PublishSubject.create();	
	private static Logger log = Logger.getRootLogger();
	
	public MqttPublisher(String broker, String clientId, String topic) {
		this.broker = broker;
		this.clientId = clientId;
		this.topic = topic;
		connect();
	}
	
	public void connect() {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			client = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(MqttConnectOptions.CLEAN_SESSION_DEFAULT);
			client.connect(options);
			client.subscribe(topic);
			client.setCallback(new MqttCallback() {
				public void connectionLost(Throwable throwable) {
					try {
						client.connect();
					} catch (MqttSecurityException ex) {
						log.error(String.format("connectionLost: %s", ex.getMessage()));
					} catch (MqttException ex) {
						log.error(String.format("connectionLost: %s", ex.getMessage()));
					}
				}
				public void deliveryComplete(IMqttDeliveryToken token) {
					log.info("onCompleted");
				}
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					subject.onNext(new String(message.getPayload()));
				}
			});
		}
		catch(MqttException ex) {
			log.error(String.format("connect exception: %s", ex.getMessage()));
			subject.onError(ex);
		}
	}
	
	public Observable<String> observe() {
		return subject;
	}
	
	public void publish(String message) {
		MqttMessage mqttMessage = new MqttMessage(message.getBytes());
		try {
			if(!client.isConnected()) {
				client.connect();
			}
			client.publish(this.topic, mqttMessage);
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

}
