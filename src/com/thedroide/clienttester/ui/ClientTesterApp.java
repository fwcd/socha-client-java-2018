package com.thedroide.clienttester.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.thedroide.clienttester.core.ClientJAR;
import com.thedroide.clienttester.core.ServerJAR;
import com.thedroide.clienttester.core.TestGameRunner;
import com.thedroide.clienttester.core.TesterJAR;

/**
 * Represents the client tester application.
 * Contains mostly GUI stuff and a lot of ugly code.
 */
public class ClientTesterApp {
	private final JFrame view;
	
	private ServerJAR server;
	private TestGameRunner runner;
	
	private final JLabel titleLabel;
	private final LoaderPane serverLoadPane;
	private final LoaderPane testerLoadPane;
	private final LoaderPane client1LoadPane;
	private final LoaderPane client2LoadPane;
	private final JSpinner portSpinner;
	private final JSpinner roundsSpinner;
	private final JButton startButton;
	
	private final OutputPane serverOutput;
	private final OutputPane testerOutput;

	private final ClientScorePane scorePane;
	
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
		
		// Main pane
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		
		JPanel loadersPane = new JPanel();
		loadersPane.setPreferredSize(new Dimension(200, 200));
		loadersPane.setLayout(new BoxLayout(loadersPane, BoxLayout.Y_AXIS));
		
		titleLabel = new JLabel("Client Tester");
		titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		titleLabel.setFont(new Font(titleLabel.getFont().getFontName(), Font.BOLD, 24)); // Change font size
		loadersPane.add(titleLabel);
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Runnable JAR-File (.jar)", "jar"));
		
		serverLoadPane = new LoaderPane(fileChooser, "Server");
		loadersPane.add(serverLoadPane.getView());
		
		testerLoadPane = new LoaderPane(fileChooser, "Tester");
		loadersPane.add(testerLoadPane.getView());
		
		client1LoadPane = new LoaderPane(fileChooser, "Client 1");
		loadersPane.add(client1LoadPane.getView());
		
		client2LoadPane = new LoaderPane(fileChooser, "Client 2");
		loadersPane.add(client2LoadPane.getView());
		
		mainPane.add(loadersPane, BorderLayout.WEST);
		
		JPanel outputPane = new JPanel();
		outputPane.setLayout(new GridLayout(2, 1));
		
		serverOutput = new OutputPane();
		outputPane.add(serverOutput.getView());
		
		testerOutput = new OutputPane();
		outputPane.add(testerOutput.getView());
		
		mainPane.add(outputPane, BorderLayout.CENTER);
		view.add(mainPane, BorderLayout.CENTER);
		
		// Options pane
		
		JPanel bottomPane = new JPanel();
		bottomPane.setPreferredSize(new Dimension(200, 200));
		bottomPane.setLayout(new BorderLayout());
		
		JToolBar optionsPane = new JToolBar();
		
		portSpinner = new JSpinner(new SpinnerNumberModel(13050, 1, 65534, 1));
		portSpinner.setEnabled(false);
		optionsPane.add(new JLabel("Server port:"));
		optionsPane.add(portSpinner);
		
		roundsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		optionsPane.add(new JLabel("Rounds:"));
		optionsPane.add(roundsSpinner);
		
		startButton = new JButton(" == START == ");
		startButton.addActionListener(l -> start());
		optionsPane.add(startButton);
		
		bottomPane.add(optionsPane, BorderLayout.NORTH);
		
		scorePane = new ClientScorePane();
		bottomPane.add(scorePane.getView(), BorderLayout.CENTER);
		
		view.add(bottomPane, BorderLayout.SOUTH);
		
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
				server = new ServerJAR(serverOutput, serverLoadPane.getName(), serverLoadPane.getFile());
			}
			
			ClientJAR client1 = new ClientJAR(client1LoadPane.getName(), client1LoadPane.getFile());
			ClientJAR client2 = new ClientJAR(client2LoadPane.getName(), client2LoadPane.getFile());
			TesterJAR tester = new TesterJAR(testerOutput, client1, client2, testerLoadPane.getName(), testerLoadPane.getFile());
			
			tester.addWinListener(out -> {
				Pattern pattern = Pattern.compile("\\d+");
				String[] results = out.split("\n");
				
				// FIXME: Still incorrect scores...
				
				int score1 = getScore(pattern, results[0]);
				int score2 = getScore(pattern, results[1]);
				
				if (score1 != -1 && score2 != -1) {
					if (score1 > score2) {
						scorePane.increment1();
					} else if (score2 > score1) {
						scorePane.increment2();
					} else {
						scorePane.increment1();
						scorePane.increment2();
					}
				}
			});
			
			scorePane.setNames(client1.getName(), client2.getName());
			
			runner = new TestGameRunner(server, tester);
			runThread = new Thread(() -> runner.start(getPort(), getRounds()), "Client Runner Thread");
			runThread.start();
		}
	}

	private int getScore(Pattern pattern, String name) {
		Matcher matcher = pattern.matcher(name);
		
		while (matcher.find()) {
			try {
				return Integer.parseInt(matcher.group());
			} catch (NumberFormatException e) {}
		}
		
		return -1;
	}
}
