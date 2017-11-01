package com.thedroide.clienttester.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TestServer extends LaunchableJAR {
	private OutputLogger mainLogger;
	private boolean foundWinner = false;
	
	public TestServer(OutputLogger output, OutputLogger mainLogger, String name, File file) {
		super(output, name, file);
		this.mainLogger = mainLogger;
	}

	public void resetWinner() {
		foundWinner = false;
	}
	
	@Override
	protected Optional<String> handleJAROut(String line) {
		if (line.contains("<winner")) {
			Map<String, String> winner = parseXMLParams(line);
			String out = winner.get("displayName")
					+ " ("
					+ winner.get("color")
					+ ") gewinnt auf Feld "
					+ winner.get("index")
					+ " mit "
					+ winner.get("carrots")
					+ " Karotten!";
			
			foundWinner = true;
			mainLogger.log(out);
		} else if (line.contains("<") || line.contains(">")) {
			return Optional.empty();
		}
		
		return Optional.of(line);
	}
	
	private Map<String, String> parseXMLParams(String tag) {
		Map<String, String> params = new HashMap<>();
		
		String key = null;
		boolean isParam = false;
		for (String fragment : tag.split("\"")) {
			if (isParam) {
				params.put(key, fragment);
			} else {
				String[] s = fragment.split(" |=");
				key = s[s.length - 1];
			}
			
			isParam = !isParam;
		}
		
		return params;
	}

	@Override
	protected String[] getLaunchCommand(int port) {
		return new String[] {"java", "-jar", getFile().getName()};
	}

	@Override
	protected boolean finished() {
		return false;
	}

	public boolean foundWinner() {
		return foundWinner;
	}
}
