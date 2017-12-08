package com.thedroide.clientsimulator.core;

import java.util.Arrays;
import java.util.NoSuchElementException;

import com.antelmann.game.AutoPlay;
import com.antelmann.game.GameDriver;
import com.antelmann.game.Player;
import com.thedroide.sc18.core.HUIGameState;

import sc.plugin2018.GameState;

public class ClientSimulator {
	private static final int TURNS = 60;
	
	private final VirtualPlayer p1;
	private final VirtualPlayer p2;
	private final AutoPlay autoPlay;
	private final HUIGameState game;

	private VirtualPlayer current;
	
	public ClientSimulator(Player strategy1, Player strategy2, int depth, long softMaxTime) {
		GameState state = new GameState();
		game = new HUIGameState(state);
		
		p1 = new VirtualPlayer(strategy1);
		p1.setColor(state.getStartPlayerColor());
		p2 = new VirtualPlayer(strategy2);
		p2.setColor(state.getStartPlayerColor().opponent());
		
		current = p1;
		
		autoPlay = new GameDriver(game, new Player[] {strategy1, strategy2}, depth);
		setSoftMaxTime(softMaxTime);
	}
	
	public void setDepth(int depth) {
		autoPlay.setLevel(depth);
	}
	
	private void setSoftMaxTime(long ms) {
		autoPlay.setResponseTime(ms);
	}
	
	private void switchTurns() {
		if (current.equals(p1)) {
			current = p2;
		} else {
			current = p1;
		}
	}
	
	private void simulate() {
		int i = 0;
		while (i < TURNS && game.getWinner() == null) {
			autoPlay.autoMove();
			switchTurns();
			i++;
		}
	}
	
	private VirtualPlayer asVirtual(Player player) {
		if (p1.getAI().equals(player)) {
			return p1;
		} else if (p2.getAI().equals(player)) {
			return p2;
		} else {
			throw new NoSuchElementException("No such virtual player available!");
		}
	}
	
	private VirtualPlayer getWinner() {
		int field1 = p1.getHUIPlayerColor().getSCPlayer(game).getFieldIndex();
		int field2 = p2.getHUIPlayerColor().getSCPlayer(game).getFieldIndex();
		
		if (field1 > field2) {
			return p1;
		} else if (field2 > field1) {
			return p2;
		} else {
			throw new NoSuchElementException("Both players are on the same field (should be impossible).");
		}
	}
	
	private void updateScores() {
		int[] winners = game.getWinner();
		
		if (winners == null) {
			getWinner().incrementScore();
		} else {
			Arrays.stream(winners)
					.mapToObj(autoPlay::getPlayer)
					.map(this::asVirtual)
					.forEach(VirtualPlayer::incrementScore);
		}
	}
	
	public void run(int gameRounds) {
		System.out.println(" ==== Client Simulator ==== ");
		System.out.println(" ~~ [" + p1.getName() + "] vs [" + p2.getName() + "] ~~ ");
		System.out.println();
		
		for (int i=0; i<gameRounds; i++) {
			simulate();
			updateScores();
			
			System.out.println(
					Integer.toString(p1.getScore())
					+ "\t:\t"
					+ Integer.toString(p2.getScore())
			);
			
			game.reset();
		}
	}
}
