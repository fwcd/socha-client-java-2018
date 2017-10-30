package com.thedroide.clienttester.core;

public class TestGameRunner {
	private final TestServer server;
	private final OutputLogger output;
	private final TestClient client1;
	private final TestClient client2;
	
	public TestGameRunner(OutputLogger output, TestServer server, TestClient client1, TestClient client2) {
		this.output = output;
		this.server = server;
		this.client1 = client1;
		this.client2 = client2;
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (server != null) {
				server.kill();
			}
			
			if (client1 != null) {
				client1.kill();
			}
			
			if (client2 != null) {
				client2.kill();
			}
		}));
	}
	
	public void start(int port, int rounds) {
		output.clear();
		
		if (!server.isAlive()) {
			server.launch(port);
			
			try {
				output.log("Starting soon...");
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				return;
			}
		}
		
		for (int i=0; i<rounds; i++) {
			output.log("\n==== Round " + Integer.toString(i) + " ====\n");
			
			client1.launch(port);
			client2.launch(port);
			
			try {
				client1.waitFor();
				client2.waitFor();
			} catch (InterruptedException e) {
				client1.kill();
				client2.kill();
				break;
			}
		}
	}
}
