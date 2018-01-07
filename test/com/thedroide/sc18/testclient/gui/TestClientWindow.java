package com.thedroide.sc18.testclient.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.thedroide.sc18.testclient.core.TestClient;
import com.thedroide.sc18.testclient.core.TestPlayer;
import com.thedroide.sc18.testclient.utils.FileConfig;

public class TestClientWindow {
	private final JFrame view;
	private boolean started = false;
	
	private Optional<FileConfig> fileConfig = loadFileConfig(getSerializationFile());
	
	private final ConfigPanel config;
	private final ConsolePanel console;
	private DualScorePane scorePane;
	
	public TestClientWindow(String title, int width, int height) {
		view = new JFrame(title);
		view.setSize(width, height);
		view.setLayout(new BorderLayout());
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		config = new ConfigPanel(true);
		Supplier<String> host = config.addStringOption("Host", "localhost");
		Supplier<Integer> port = config.addIntOption("Port", 13050, 0, 65535);
		Supplier<Integer> tests = config.addIntOption("Tests", 100, 1, 5000);
		Supplier<File> serverProperties = config.addFileOption(
				"Choose server.properties",
				"properties",
				fileConfig.map(f -> f.get("serverProperties")).orElse(null)
		);
		config.nextSection();
		Supplier<TestPlayer> player1 = addPlayerConfig("Player 1", fileConfig.map(f -> f.get("player1Jar")));
		config.nextSection();
		Supplier<TestPlayer> player2 = addPlayerConfig("Player 2", fileConfig.map(f -> f.get("player2Jar")));
		config.nextSuperSection();
		config.addButton(" === START TestClient (Start server seperately before) === ", () -> {
			try {
				if (started) {
					JOptionPane.showMessageDialog(view, "TestClient already launched!");
				} else {
					start(
							host.get(),
							port.get(),
							tests.get(),
							player1.get(),
							player2.get(),
							serverProperties.get()
					);
				}
			} catch (NoSuchElementException e) {
				JOptionPane.showMessageDialog(view, "Please fill in configuration!");
			}
		});
		view.add(config.getView(), BorderLayout.NORTH);
		
		console = new ConsolePanel();
		console.setHeight(100);
		view.add(console.getView(), BorderLayout.SOUTH);
		
		view.setVisible(true);
	}
	
	private File getSerializationFile() {
		return new File(System.getProperty("user.home") + "/.sochacache/testClientFileConfig.txt");
	}

	private Optional<FileConfig> loadFileConfig(File file) {
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file); ObjectInputStream ois = new ObjectInputStream(fis)) {
				return Optional.of((FileConfig) ois.readObject());
			} catch (Exception e) {}
		}
		
		return Optional.empty();
	}
	
	private void saveFileConfig(File file) {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}
		try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			fileConfig.ifPresent(conf -> {
				try {
					oos.writeObject(conf);
				} catch (IOException e) {}
			});
		} catch (Exception e) {}
	}
	
	private void start(String host, int port, int tests, TestPlayer player1, TestPlayer player2, File serverProperties) {
		scorePane = new DualScorePane(player1.getDisplayName(), player2.getDisplayName());
		view.add(scorePane.getView(), BorderLayout.CENTER);
		view.revalidate();
		view.repaint();
		
		FileConfig newFileConf = new FileConfig();
		newFileConf.put("serverProperties", serverProperties);
		newFileConf.put("player1Jar", new File(player1.getPathToJar()));
		newFileConf.put("player2Jar", new File(player2.getPathToJar()));
		fileConfig = Optional.of(newFileConf);
		saveFileConfig(getSerializationFile());
		
		try (FileReader reader = new FileReader(serverProperties)) {
			sc.server.Configuration.load(reader);
			
			started = true;
			new TestClient(
					sc.server.Configuration.getXStream(),
					sc.plugin2018.util.Configuration.getClassesToRegister(),
					host,
					port,
					tests,
					player1,
					player2,
					scorePane,
					console
			);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(view, e.getClass().getSimpleName() + " (" + e.getMessage() + ") while starting!\n(Did you start the server?)");
		}
	}
	
	private Supplier<TestPlayer> addPlayerConfig(String label, Optional<File> jar) {
		ConfigPanel panel = config.addSubPanel(label);
		
		Supplier<String> name = panel.addStringOption("Name", label);
		Supplier<Boolean> timeouts = panel.addBoolOption("With Timeouts", true);
		Supplier<File> file = panel.addFileOption("Select JAR", "jar", jar.orElse(null));
		
		final String defaultName = name.get();
		
		return () -> {
			File fetchedFile = file.get();
			String fetchedName = name.get();
			if (fetchedName.equals(defaultName)) {
				fetchedName = fetchedFile.getName();
			}
			return new TestPlayer(timeouts.get(), fetchedName, fetchedFile.getAbsolutePath());
		};
	}
}
