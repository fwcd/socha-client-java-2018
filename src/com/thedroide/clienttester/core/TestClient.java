package com.thedroide.clienttester.core;

import java.io.File;
import java.util.Optional;

public class TestClient extends LaunchableJAR {
	public TestClient(OutputLogger output, String name, File file) {
		super(output, name, file);
	}

	@Override
	protected Optional<String> handleJAROut(String line) {
		return Optional.of(line);
	}
}
