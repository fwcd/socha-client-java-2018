package com.fwcd.sc18.trainer.ui;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import com.fwcd.sc18.trainer.core.GameSimulator;
import com.fwcd.sc18.trainer.core.VirtualClient;

import sc.plugin2018.IGameHandler;
import sc.shared.PlayerColor;

public class TrainerApp {
	private final JFrame view;
	
	private final GameView gameView;
	private final PopulationPanel p1;
	private final PopulationPanel p2;
	
	private GameSimulator simulator;
	
	public TrainerApp(String title, int width, int height) {
		view = new JFrame(title);
		view.setSize(width, height);
		view.setLayout(new BorderLayout());
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		content.setBorder(new EmptyBorder(0, 0, 0, 0));
		content.setDividerLocation(400);
		
		JSplitPane sideBar = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sideBar.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		gameView = new GameView();
		sideBar.setTopComponent(gameView.getView());
		
		ConfigPanel viewConfig = new ConfigPanel(true, false);
		ConfigPanel subConfig = viewConfig.addSubPanel("Loader");
		BooleanSupplier viewBound = subConfig.addBoolOption("Bind view", true);
		Supplier<String> classA = subConfig.addStringOption("Logic A", "sc.player2018.RandomLogic");
		Supplier<String> classB = subConfig.addStringOption("Logic B", "com.fwcd.sc18.geneticneural.GeneticNeuralLogic");
		IntSupplier matches = subConfig.addIntOption("Matches", 1, 1, Integer.MAX_VALUE);
		subConfig.addButton(" === Play === ", () -> play(viewBound.getAsBoolean(), classA.get(), classB.get(), matches.getAsInt()));
		sideBar.setBottomComponent(viewConfig.getView());
		
		content.setLeftComponent(sideBar);
		
		JSplitPane center = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane populationPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		populationPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		p1 = new PopulationPanel(1);
		populationPane.setLeftComponent(p1.getView());
		
		p2 = new PopulationPanel(2);
		populationPane.setRightComponent(p2.getView());
		
		ConsolePane console = new ConsolePane();
		System.setOut(console.getOutStream());
		System.setErr(console.getErrStream());
		center.setBottomComponent(console.getView());
		
		center.setTopComponent(populationPane);
		content.setRightComponent(center);
		
		view.add(content, BorderLayout.CENTER);
		view.setVisible(true);
		sideBar.setDividerLocation(0.8D);
		populationPane.setDividerLocation(0.5D);
		
		center.setDividerLocation(0.8D);
	}
	
	private void onGameEnd() {
		p1.reload();
		p1.getView().repaint();
		p2.reload();
		p2.getView().repaint();
	}
	
	private void play(boolean viewBound, String classA, String classB, int matches) {
		if (simulator != null) {
			simulator.stop();
		}
		
		try {
			VirtualClient redClient = new VirtualClient(PlayerColor.RED);
			VirtualClient blueClient = new VirtualClient(PlayerColor.BLUE);
			IGameHandler logicA = (IGameHandler) Class.forName(classA).getConstructor(VirtualClient.class).newInstance(redClient);
			IGameHandler logicB = (IGameHandler) Class.forName(classB).getConstructor(VirtualClient.class).newInstance(blueClient);
			simulator = new GameSimulator(
					viewBound ? Optional.of(gameView) : Optional.empty(),
					logicA,
					logicB,
					redClient,
					blueClient,
					matches,
					Collections.singletonList(this::onGameEnd)
			);
			new Thread(simulator::run).start();
		} catch (ReflectiveOperationException e) {
			error("Your logic class is missing the required 1-argument VirtualClient constructor!\n\n"
					+ "(If you do add such a constructor, place the VirtualClient\n"
					+ "instance as a field into your logic class and make sure to call\n"
					+ "it respectively in sendAction() by checking whether we're dealing with\n"
					+ "a real client (an 'AbstractClient' instance) or a virtual client.)"
			);
		}
	}

	protected void error(String msg) {
		JOptionPane.showMessageDialog(view, msg, "Error while starting game", JOptionPane.ERROR_MESSAGE);
	}
}
