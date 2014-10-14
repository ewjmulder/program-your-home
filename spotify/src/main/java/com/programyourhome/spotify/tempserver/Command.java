package com.programyourhome.spotify.tempserver;

public enum Command {

	// TODO: Type safe way of defining this in Scala?
	
	PLAY(0),
	PAUSE(0),
	STOP(0),
	START(2),
	SEARCH(1);
	
	private int numberOfArguments;
	
	private Command(int numberOfArguments) {
		this.numberOfArguments = numberOfArguments;
	}
	
	public int getNumberOfArguments() {
		return numberOfArguments;
	}
	
}
