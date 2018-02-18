package com.joel.iot.commands;

public class RestGetCommand extends RestCommand {

	private String[] parameters;
	
	public RestGetCommand(String name, String subject, String command, String url, String[] parameters) {
		super(name, subject, command, url);
		this.parameters = parameters;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}
	
}
