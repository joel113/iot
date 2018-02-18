package com.joel.iot.commands;

public class RestPutCommand extends RestCommand {

	private String body;

	public RestPutCommand(String name, String subject, String command, String url, String body) {
		super(name, subject, command, url);
		this.body = body;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
}
