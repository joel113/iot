package com.joel.iot.commands;

import java.util.ArrayList;
import java.util.List;

public class RestCommands {
	
	List<RestGetCommand> restgetcommands = new ArrayList<RestGetCommand>();
	List<RestPutCommand> restputcommands = new ArrayList<RestPutCommand>();
	
	public List<RestGetCommand> getRestgetcommands() {
		return restgetcommands;
	}
	
	public void setRestgetcommands(List<RestGetCommand> restgetcommands) {
		this.restgetcommands = restgetcommands;
	}
	
	public List<RestPutCommand> getRestputcommands() {
		return restputcommands;
	}
	
	public void setRestputcommands(List<RestPutCommand> restputcommands) {
		this.restputcommands = restputcommands;
	}
		
}
