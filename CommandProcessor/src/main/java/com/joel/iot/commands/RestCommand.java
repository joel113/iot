package com.joel.iot.commands;

public class RestCommand {

	private String name;
	private String subject;
	private String command;
	private String url;
	
	public RestCommand(String name, String subject, String command, String url) {
		super();
		this.name = name;
		this.subject = subject;
		this.command = command;
		this.url = url;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
