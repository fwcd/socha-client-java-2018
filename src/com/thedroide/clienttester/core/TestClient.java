package com.thedroide.clienttester.core;

import java.io.File;
import java.util.Optional;

public class TestClient extends LaunchableJAR {
	private final TestServer server;
	public TestClient(OutputLogger output, TestServer server, String name, File file) {
		super(output, name, file);
		this.server = server;
	}

	@Override
	protected Optional<String> handleJAROut(String line) {
		if (line.contains("<") || line.contains(">")) {
			return Optional.empty();
		} else {
			return Optional.of(line);
		}
	}

	@Override
	protected boolean finished() {
		return server.foundWinner();
	}
}
