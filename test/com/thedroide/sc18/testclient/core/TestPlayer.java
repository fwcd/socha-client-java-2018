package com.thedroide.sc18.testclient.core;

public class TestPlayer {
	private final boolean canTimeout;
	private final String displayName;
	private final String pathToJar;
	
	public TestPlayer(boolean canTimeout, String displayName, String pathToJar) {
		this.canTimeout = canTimeout;
		this.displayName = displayName;
		this.pathToJar = pathToJar;
	}

	public boolean canTimeout() {
		return canTimeout;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPathToJar() {
		return pathToJar;
	}
}
