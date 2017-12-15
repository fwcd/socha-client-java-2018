package com.thedroide.clientsimulator.core;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.antelmann.game.AutoPlay;
import com.antelmann.game.GameDriver;
import com.antelmann.game.Player;
import com.thedroide.sc18.core.HUIGameState;

import sc.plugin2018.GameState;

public class ClientSimulator {
	private static final int TURNS = 60;
	
	private final VirtualPlayer p1;
	private final VirtualPlayer p2;

	private int gameRounds = 5;
	private int threadCount = 1;
	private ExecutorService pool = Executors.newSingleThreadExecutor();

	private long softMaxTime = Long.MAX_VALUE;
	private int depth = 2;
	
	public ClientSimulator(Player strategy1, Player strategy2) {
		GameState state = new GameState();
		
		p1 = new VirtualPlayer(strategy1);
		p1.setColor(state.getStartPlayerColor());
		p2 = new VirtualPlayer(strategy2);
		p2.setColor(state.getStartPlayerColor().opponent());
	}
	
	public ClientSimulator setDepth(int depth) {
		this.depth = depth;
		return this;
	}
	
	public ClientSimulator setSoftMaxTime(long ms) {
		softMaxTime = ms;
		return this;
	}
	
	public ClientSimulator setThreadCount(int threadCount) {
		this.threadCount = threadCount;
		pool = Executors.newFixedThreadPool(threadCount);
		return this;
	}
	
	public ClientSimulator setGameRounds(int rounds) {
		gameRounds = rounds;
		return this;
	}
	
	private VirtualPlayer opponentOf(VirtualPlayer current) {
		if (current.equals(p1)) {
			return p2;
		} else {
			return p1;
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
	
	private VirtualPlayer getWinner(HUIGameState game) {
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
	
	private void updateScores(AutoPlay autoPlay, HUIGameState game) {
		int[] winners = game.getWinner();
		
		if (winners == null) {
			getWinner(game).incrementScore();
		} else {
			Arrays.stream(winners)
					.mapToObj(autoPlay::getPlayer)
					.map(this::asVirtual)
					.forEach(VirtualPlayer::incrementScore);
		}
	}
	
	public void start() {
		System.out.println(" ==== Client Simulator ==== ");
		System.out.println(" ~~ [" + Integer.toString(gameRounds) + " rounds] - [" + Integer.toString(threadCount) + " threads] ~~");
		System.out.println(" ~~ [" + p1.getName() + "] vs [" + p2.getName() + "] ~~ ");
		System.out.println();
		
		for (int i=0; i<gameRounds; i++) {
			pool.execute(this::run);
		}
		
		pool.shutdown();
	}
	
	private void run() {
		VirtualPlayer current = p1;
		HUIGameState game = new HUIGameState();
		AutoPlay autoPlay = new GameDriver(game, new Player[] {p1.getAI(), p2.getAI()}, depth);
		
		autoPlay.setResponseTime(softMaxTime);
		
		int t = 0;
		while (t < TURNS && game.getWinner() == null) {
			autoPlay.autoMove();
			current = opponentOf(current);
			t++;
		}
		
		updateScores(autoPlay, game);
		
		System.out.println(
				Integer.toString(p1.getScore())
				+ "\t:\t"
				+ Integer.toString(p2.getScore())
				+ "\t[Thread "
				+ Integer.toHexString(Thread.currentThread().hashCode())
				+ "]"
		);
	}
}
