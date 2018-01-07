package com.thedroide.sc18.testclient.utils;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FileConfig implements Serializable {
	private static final long serialVersionUID = -2513020001718114728L;
	private Map<String, String> files;
	
	public FileConfig() {
		files = new HashMap<>();
	}
	
	public void put(String name, File file) {
		files.put(name, file.getAbsolutePath());
	}
	
	public File get(String name) {
		return new File(files.get(name));
	}
}
