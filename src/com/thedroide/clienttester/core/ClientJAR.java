package com.thedroide.clienttester.core;

import java.io.File;

public class ClientJAR {
	private final String name;
	private final File file;

	public ClientJAR(String name, File file) {
		this.name = name;
		this.file = file;
	}
	
	public String getName() {
		return name;
	}
	
	public File getFile() {
		return file;
	}
}
