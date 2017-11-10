package com.thedroide.clienttester.core;

import java.util.function.BooleanSupplier;

public class TestGameRunner {
	private final ServerJAR server;
	private final TesterJAR tester;
	
	public TestGameRunner(ServerJAR server, TesterJAR tester) {
		this.server = server;
		this.tester = tester;
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (server != null) {
				server.kill();
			}
			
			if (tester != null) {
				tester.kill();
			}
		}));
	}
	
	private void waitUntil(BooleanSupplier condition, long timeout) {
		long start = System.currentTimeMillis();
		
		while (!condition.getAsBoolean() && (System.currentTimeMillis() - start) < timeout) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void start(int port, int rounds) {
		if (!server.isAlive()) {
			server.launch(port);
			waitUntil(server::isReady, 1500);
		}
		
		tester.launch(port, rounds);
		
		try {
			tester.waitFor();
		} catch (InterruptedException e) {
			tester.kill();
		}
	}
}
