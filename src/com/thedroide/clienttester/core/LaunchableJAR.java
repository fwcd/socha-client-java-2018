package com.thedroide.clienttester.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public abstract class LaunchableJAR {
	private final String name;
	private final File file;
	private final OutputLogger output;
	private Process process = null;
	
	public LaunchableJAR(OutputLogger output, String name, File file) {
		this.output = output;
		this.name = name;
		this.file = file;
	}
	
	public String getName() {
		return name;
	}
	
	protected File getFile() {
		return file;
	}
	
	protected abstract Optional<String> handleJAROut(String line);
	
	protected String[] getLaunchCommand(int port) {
		return new String[] {"java", "-jar", file.getName(), "--port", Integer.toString(port)};
	}
	
	public void launch(int port) {
		new Thread(() -> {
			kill();
			
			ProcessBuilder pb = new ProcessBuilder(getLaunchCommand(port));
			pb.directory(file.getParentFile());
			try {
				process = pb.start();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					
					while ((line = reader.readLine()) != null) {
						handleJAROut(line).ifPresent(output::log);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, "Running JAR Thread").start();
	}
	
	protected abstract boolean finished();
	
	public void waitFor() throws InterruptedException {
		while (!isAlive() || !finished()) {
			Thread.sleep(100);
		}
	}
	
	public void kill() {
		if (isAlive()) {
			process.destroy();
		}
	}

	public boolean isAlive() {
		return process == null ? false : process.isAlive();
	}
}
