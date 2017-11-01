package com.thedroide.clienttester.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.thedroide.clienttester.core.TestClient;
import com.thedroide.clienttester.core.TestGameRunner;
import com.thedroide.clienttester.core.TestServer;

/**
 * Represents the client tester application.
 * Contains mostly GUI stuff and a lot of ugly code.
 */
public class ClientTesterApp {
	private final JFrame view;
	
	private TestServer server;
	private TestGameRunner runner;
	
	private final JLabel titleLabel;
	private final LoaderPane serverLoadPane;
	private final LoaderPane client1LoadPane;
	private final LoaderPane client2LoadPane;
	private final JSpinner portSpinner;
	private final JSpinner roundsSpinner;
	private final JButton startButton;
	
	private final OutputPane mainOutput;
	private final OutputPane serverOutput;
	private final OutputPane client1Output;
	private final OutputPane client2Output;

	private Thread runThread;
	
	public ClientTesterApp(String title, int width, int height) {
		view = new JFrame(title);
		view.setSize(width, height);
		view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		view.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		view.setLayout(new BorderLayout());
		
		// Title and client load buttons
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		
		titleLabel = new JLabel("Client Tester");
		titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		titleLabel.setFont(new Font(titleLabel.getFont().getFontName(), Font.BOLD, 24)); // Change font size
		titlePanel.add(titleLabel);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Client JAR-File (.jar)", "jar"));
		
		serverLoadPane = new LoaderPane(fileChooser, false, 0);
		buttonsPanel.add(serverLoadPane.getView());
		
		client1LoadPane = new LoaderPane(fileChooser, true, 1);
		buttonsPanel.add(client1LoadPane.getView());
		
		client2LoadPane = new LoaderPane(fileChooser, true, 2);
		buttonsPanel.add(client2LoadPane.getView());
		
		titlePanel.add(buttonsPanel);
		
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
		
		portSpinner = new JSpinner(new SpinnerNumberModel(13050, 1, 65534, 1));
		portSpinner.setEnabled(false);
		spinnerPanel.add(new JLabel("Server port:"));
		spinnerPanel.add(portSpinner);
		
		roundsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		spinnerPanel.add(new JLabel("Rounds:"));
		spinnerPanel.add(roundsSpinner);
		
		titlePanel.add(spinnerPanel);
		view.add(titlePanel, BorderLayout.NORTH);
		
		// Output panel
		
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BorderLayout());
		
		JPanel directOutputsPanel = new JPanel();
		directOutputsPanel.setLayout(new GridLayout(1, 3));
		
		serverOutput = new OutputPane();
		directOutputsPanel.add(serverOutput.getView());
		
		client1Output = new OutputPane();
		directOutputsPanel.add(client1Output.getView());
		
		client2Output = new OutputPane();
		directOutputsPanel.add(client2Output.getView());
		
		outputPanel.add(directOutputsPanel, BorderLayout.CENTER);
		
		mainOutput = new OutputPane();
		outputPanel.add(mainOutput.getView(), BorderLayout.SOUTH);
		
		view.add(outputPanel, BorderLayout.CENTER);
		
		// Start button
		
		startButton = new JButton(" == START == ");
		startButton.addActionListener(l -> start());
		view.add(startButton, BorderLayout.SOUTH);
		
		view.setVisible(true);
	}

	private int getPort() {
		return (Integer) portSpinner.getModel().getValue();
	}
	
	private int getRounds() {
		return (Integer) roundsSpinner.getModel().getValue();
	}
	
	private void start() {
		if (runThread != null && runThread.isAlive()) {
			runThread.interrupt();
			try {
				runThread.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
		if (serverLoadPane.validAndSelected()
				&& client1LoadPane.validAndSelected()
				&& client2LoadPane.validAndSelected()
		) {
			if (server == null) {
				server = new TestServer(serverOutput, mainOutput, serverLoadPane.getName(), serverLoadPane.getFile());
			}
			
			TestClient client1 = new TestClient(client1Output, server, client1LoadPane.getName(), client1LoadPane.getFile());
			TestClient client2 = new TestClient(client2Output, server, client2LoadPane.getName(), client2LoadPane.getFile());
			
			runner = new TestGameRunner(mainOutput, server, client1, client2);
			runThread = new Thread(() -> runner.start(getPort(), getRounds()), "Client Runner Thread");
			
			runThread.start();
		}
	}
}
