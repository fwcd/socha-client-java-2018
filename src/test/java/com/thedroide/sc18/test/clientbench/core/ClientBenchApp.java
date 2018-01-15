package com.thedroide.sc18.test.clientbench.core;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.thedroide.sc18.alphabeta.AlphaBetaPlayer;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.test.clientbench.utils.SimpleButton;

import sc.plugin2018.GameState;

public class ClientBenchApp {
	private final JFrame view;
	
	private final JToolBar toolBar;
	private final GameView game;
	
	public ClientBenchApp() {
		view = new JFrame("ClientBench");
		view.setSize(640, 480);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setLayout(new BorderLayout());

		game = new GameView();
		view.add(game.getView(), BorderLayout.CENTER);
		
		toolBar = new JToolBar();
		toolBar.add(new SimpleButton("New Game", () -> game.update(new HUIGameState(new GameState()))));
		toolBar.add(new SimpleButton("Simulate", this::simulate));
		view.add(toolBar, BorderLayout.NORTH);
		
		view.setVisible(true);
	}
	
	public void simulate() {
		new Benchmarker()
				.add(new AlphaBetaPlayer())
				.add(new AlphaBetaPlayer())
				.bind(game)
				.setGameRounds(100)
				.setSoftMaxTime(1000)
				.start();
	}
}
