package com.joel.iot.commands;

public class Command {

	private String subject;
	private String command;
	
	public Command(String subject, String command) {
		super();
		this.subject = subject;
		this.command = command;
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

}
