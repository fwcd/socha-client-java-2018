package com.thedroide.clienttester.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TesterJAR extends LaunchableJAR {
	private final ClientJAR client1;
	private final ClientJAR client2;
	private int rounds;
	private List<Consumer<String>> listeners = new ArrayList<>();
	private String firstScore = null;
	
	public TesterJAR(OutputLogger output, ClientJAR client1, ClientJAR client2, String name, File file) {
		super(output, name, file);
		
		this.client1 = client1;
		this.client2 = client2;
	}

	@Override
	protected Optional<String> handleJAROut(String line) {
		if (line.contains("<") || line.contains(">")) {
			return Optional.empty();
		} else if (line.contains("Received new score for")) {
			String trimmed = line.split("Received new score for")[1].trim();
			
			if (firstScore == null) {
				firstScore = trimmed;
			} else {
				fireListeners(firstScore + "\n" + trimmed);
				firstScore = null;
			}
		}
		
		return Optional.of(line);
	}
	
	private void fireListeners(String line) {
		for (Consumer<String> listener : listeners) {
			listener.accept(line);
		}
	}
	
	public void addWinListener(Consumer<String> outputListener) {
		listeners.add(outputListener);
	}
	
	@Override
	protected String[] getLaunchCommand(int port) {
		return new String[] {
				"java", "-jar", getFile().getName(),
				"--port", Integer.toString(port),
				"--tests", Integer.toString(rounds),
				
				"--name1", client1.getName(),
				"--player1", client1.getFile().getAbsolutePath(),
				"--timeout1", "true",
				
				"--name2", client2.getName(),
				"--player2", client2.getFile().getAbsolutePath(),
				"--timeout2", "true"
		};
		
	}
	
	public void launch(int port, int rounds) {
		this.rounds = rounds;
		launch(port);
	}
}
