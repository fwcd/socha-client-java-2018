package com.thedroide.sc18.testclient.core;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

import com.thedroide.sc18.testclient.gui.ConsolePanel;
import com.thedroide.sc18.testclient.gui.DualScorePane;
import com.thoughtworks.xstream.XStream;

import sc.networking.INetworkInterface;
import sc.networking.TcpNetwork;
import sc.networking.clients.XStreamClient;
import sc.protocol.LobbyProtocol;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.GetScoreForPlayerRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.requests.TestModeRequest;
import sc.protocol.responses.CloseConnection;
import sc.protocol.responses.PlayerScorePacket;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.protocol.responses.RoomPacket;
import sc.protocol.responses.TestModeMessage;
import sc.shared.GameResult;
import sc.shared.Score;
import sc.shared.SlotDescriptor;

public class TestClient extends XStreamClient {
	private static final String GAME_TYPE = "swc_2018_hase_und_igel";

	private final Logger log;
	private final String host;
	private final int port;
	private final int numberOfTests;
	private final TestPlayer player1;
	private final TestPlayer player2;
	private final DualScorePane scorePane;
	
	private int currentTests;
	private boolean terminateWhenPossible = false;
	private int gotLastPlayerScores = 0;

	private List<Score> scores = new LinkedList<>();

	private Process proc1;
	private Process proc2;


	public TestClient(
			XStream xstream,
			Collection<Class<?>> protocolClasses,
			String host,
			int port,
			int numberOfTests,
			TestPlayer player1,
			TestPlayer player2,
			DualScorePane scorePane,
			ConsolePanel console
	) throws IOException {
		super(xstream, createTcpNetwork(host, port));
		
		LobbyProtocol.registerMessages(xstream);
		LobbyProtocol.registerAdditionalMessages(xstream, protocolClasses);
		
		this.player1 = player1;
		this.player2 = player2;
		this.host = host;
		this.port = port;
		this.numberOfTests = numberOfTests;
		this.scorePane = scorePane;
		log = console.asLogger();
		
		start();
		log.info("Authenticate as administrator");
		send(new AuthenticateRequest(sc.server.Configuration.getAdministrativePassword()));
		log.info("Enabling TestMode");
		send(new TestModeRequest(true));
		log.info("Waiting for input of displayName to print players current Score");
	}

	@Override
	protected void onObject(ProtocolMessage o) throws sc.networking.UnprocessedPacketException {
		if (o == null) {
			log.warn("Received null object.");
		} else if ((o instanceof TestModeMessage)) {
			boolean pgm = ((TestModeMessage) o).testMode;
			log.info("TestMode was set to {}, starting clients", Boolean.valueOf(pgm));
			startNewClients();
		} else if ((o instanceof RoomPacket)) {
			RoomPacket packet = (RoomPacket) o;
			if ((packet.getData() instanceof GameResult)) {
				log.warn("Received game result");
				currentTests += 1;
				send(new GetScoreForPlayerRequest(player1.getDisplayName()));
				send(new GetScoreForPlayerRequest(player2.getDisplayName()));
				proc1.destroyForcibly();
				proc2.destroyForcibly();
				if (currentTests == numberOfTests) {
					terminateWhenPossible = true;
				}

				startNewClients();
			}
		} else if ((o instanceof PlayerScorePacket)) {
			if (terminateWhenPossible) {
				gotLastPlayerScores += 1;
			}
			Score score = ((PlayerScorePacket) o).getScore();

			if (scores.size() < 2) {
				scores.add(score);
			} else {
				for (Score s : scores) {
					if (s.getDisplayName().equals(score.getDisplayName())) {
						scores.add(score);
						scores.remove(s);
						break;
					}
				}
			}
			
			try {
				scorePane.getGUI(score.getDisplayName())
						.update(score, currentTests, numberOfTests);
			} catch (NoSuchElementException e) {
				log.warn("Could not find ScoreGUI of " + score.getDisplayName());
			}
			
//			log.warn("Received new score for " + score.getDisplayName() + ": Siegpunkte "
//					+ ((ScoreValue) score.getScoreValues().get(0)).getValue() + ", Durchschnittl. Feldnummer "
//					+ ((ScoreValue) score.getScoreValues().get(1)).getValue() + ", Durchschnittl. Karotten "
//					+ ((ScoreValue) score.getScoreValues().get(2)).getValue() + " after " + currentTests + " of "
//					+ numberOfTests + " tests");
			if (gotLastPlayerScores == 2) {
				send(new CloseConnection());
			}
		} else if ((o instanceof PrepareGameProtocolMessage)) {
			log.info("Trying to start clients");
			PrepareGameProtocolMessage pgm = (PrepareGameProtocolMessage) o;
			send(new ObservationRequest(pgm.getRoomId()));
			try {
				log.info("Trying first client {}", player1.getPathToJar());
				String startClient1 = "java -jar " + player1.getPathToJar() + " -r " + (String) pgm.getReservations().get(currentTests % 2)
						+ " -h " + host + " -p " + port;
				String startClient2 = "java -jar " + player2.getPathToJar() + " -r "
						+ (String) pgm.getReservations().get((currentTests + 1) % 2) + " -h " + host + " -p " + port;
				proc1 = Runtime.getRuntime().exec(startClient1);
				log.info("Trying second client {}", player2.getPathToJar());
				proc2 = Runtime.getRuntime().exec(startClient2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			log.info("Received packet I am not interested in");
		}
	}

	private static INetworkInterface createTcpNetwork(String host, int port) throws IOException {
		return new TcpNetwork(new Socket(host, port));
	}

	private void startNewClients() {
		if (currentTests != numberOfTests) {
			SlotDescriptor slot2;
			SlotDescriptor slot1;
			if (currentTests % 2 == 0) {
				slot1 = new SlotDescriptor(player1.getDisplayName(), player1.canTimeout(), false);
				slot2 = new SlotDescriptor(player2.getDisplayName(), player2.canTimeout(), false);
			} else {
				slot1 = new SlotDescriptor(player2.getDisplayName(), player2.canTimeout(), false);
				slot2 = new SlotDescriptor(player1.getDisplayName(), player1.canTimeout(), false);
			}
			send(new PrepareGameRequest(GAME_TYPE, slot1, slot2));
		}
	}
}