package com.thedroide.sc18.test.clientbench.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.antelmann.game.Player;

/**
 * Simulates multiple players by letting each player
 * compete against every other player per round.
 */
public class Benchmarker {
	private boolean started = false;
	private boolean muted = false;
	private final Map<Player, Integer> players = new LinkedHashMap<>(); // Players and scores
	
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	private int depth = 2;
	private long softMaxTime = Long.MAX_VALUE;
	private int gameRounds = 5;
	private Optional<GameView> boundGame = Optional.empty();
	
	public Benchmarker add(Player player) {
		players.put(player, 0);
		return this;
	}
	
	public Benchmarker setDepth(int depth) {
		this.depth = depth;
		return this;
	}
	
	public Benchmarker setSoftMaxTime(long ms) {
		softMaxTime = ms;
		return this;
	}
	
	public Benchmarker setMuted(boolean muted) {
		this.muted = muted;
		return this;
	}

	public Benchmarker setGameRounds(int gameRounds) {
		this.gameRounds  = gameRounds;
		return this;
	}
	
	public Benchmarker setThreadCount(int threads) {
		pool = Executors.newFixedThreadPool(threads);
		return this;
	}

	public Benchmarker bind(GameView game) {
		boundGame = Optional.of(game);
		return this;
	}
	
	/**
	 * Simulates a match between the given two players
	 * and updates the scores.
	 * 
	 * @param a - Player A
	 * @param b - Player B
	 */
	private void simulate(Player a, Player b) {
		GameSimulator simulator = new GameSimulator(a, b)
				.setDepth(depth)
				.setSoftMaxTime(softMaxTime)
				.setMuted(true);
		boundGame.ifPresent(simulator::bind);
		simulator.run();
		
		int score1 = simulator.getPlayer1().getScore();
		int score2 = simulator.getPlayer2().getScore();
		
		if (score1 > score2) {
			players.put(a, players.get(a) + 1);
		} else if (score1 < score2) {
			players.put(b, players.get(b) + 1);
		}
	}
	
	public void start() {
		if (started) {
			throw new IllegalStateException("Benchmarker already has been started.");
		} else {
			started = true;
		}
		
		if (!muted) {
			System.out.println(" ==== Client Bench ==== ");
			System.out.println();
		}
		
		for (int i=0; i<gameRounds; i++) {
			for (Player playerA : players.keySet()) {
				for (Player playerB : players.keySet()) {
					if (playerA != playerB) {
						pool.execute(() -> simulateAndPrint(playerA, playerB));
					}
				}
			}
		}
	}
	
	public void waitFor() {
		try {
			pool.shutdown();
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {}
	}

	public int getScore(Player player) {
		return players.get(player);
	}
	
	private void simulateAndPrint(Player playerA, Player playerB) {
		simulate(playerA, playerB);
		printScores();
	}
	
	private void printScores() {
		for (Player player : players.keySet()) {
			Integer score = players.get(player);
			
			if (!muted) {
				System.out.print("[" + player.getPlayerName() + ":] " + score.toString() + "\t\t");
			}
		}
		
		if (!muted) {
			System.out.println();
		}
	}
}
