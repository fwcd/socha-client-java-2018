package com.thedroide.sc18.testclient;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.thedroide.sc18.testclient.gui.TestClientWindow;

public class TestClientMain {
	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}
		
		new TestClientWindow("TestClient", 640, 480);
		
//		CmdLineParser parser = new CmdLineParser();
//		CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
//		CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
//		CmdLineParser.Option numberOfTestsOption = parser.addIntegerOption("tests");
//		CmdLineParser.Option playerJar1Option = parser.addStringOption("player1");
//		CmdLineParser.Option playerJar2Option = parser.addStringOption("player2");
//		CmdLineParser.Option name1Option = parser.addStringOption("name1");
//		CmdLineParser.Option name2Option = parser.addStringOption("name2");
//		CmdLineParser.Option p1CanTimeoutOption = parser.addBooleanOption("timeout1");
//		CmdLineParser.Option p2CanTimeoutOption = parser.addBooleanOption("timeout2");
//		
//		try {
//			parser.parse(args);
//		} catch (CmdLineParser.OptionException e) {
//			System.exit(2);
//		}
//		try {
//			LOG.error("loading server.properties");
//			sc.server.Configuration.load(new FileReader("server.properties"));
//		} catch (IOException e) {
//			LOG.error("Could not find server.properties", e);
//			throw new RuntimeException(e);
//		}
//
//		String host = (String) parser.getOptionValue(hostOption, "localhost");
//		int port = (int) parser.getOptionValue(portOption, 13050);
//		int numberOfTests = (int) parser.getOptionValue(numberOfTestsOption, 100);
//		TestPlayer player1 = new TestPlayer(
//				(boolean) parser.getOptionValue(p1CanTimeoutOption, true),
//				(String) parser.getOptionValue(name1Option, "player1"),
//				(String) parser.getOptionValue(playerJar1Option)
//		);
//		TestPlayer player2 = new TestPlayer(
//				(boolean) parser.getOptionValue(p2CanTimeoutOption, true),
//				(String) parser.getOptionValue(name2Option, "player2"),
//				(String) parser.getOptionValue(playerJar2Option)
//		);
//		
//		try {
//			new TestClient(
//					sc.server.Configuration.getXStream(),
//					sc.plugin2018.util.Configuration.getClassesToRegister(),
//					host,
//					port,
//					numberOfTests,
//					player1,
//					player2
//			);
//		} catch (Exception e) {
//			LOG.error("Error on startup: ", e);
//			throw new RuntimeException(e);
//		}
	}
}
