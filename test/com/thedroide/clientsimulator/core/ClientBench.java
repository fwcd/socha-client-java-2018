package com.thedroide.clientsimulator.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.antelmann.game.Player;

/**
 * Simulates multiple players by letting each player
 * compete against every other player per round.
 * 
 * @author Fredrik
 *
 */
public class ClientBench {
	private boolean muted = false;
	private final Map<Player, Integer> players = new LinkedHashMap<>(); // Players and scores
	
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	private int depth = 2;
	private long softMaxTime = Long.MAX_VALUE;
	private int gameRounds = 5;
	
	public ClientBench add(Player player) {
		players.put(player, 0);
		return this;
	}
	
	public ClientBench setDepth(int depth) {
		this.depth = depth;
		return this;
	}
	
	public ClientBench setSoftMaxTime(long ms) {
		softMaxTime = ms;
		return this;
	}

	public ClientBench setGameRounds(int gameRounds) {
		this.gameRounds  = gameRounds;
		return this;
	}
	
	public ClientBench setThreadCount(int threads) {
		pool = Executors.newFixedThreadPool(threads);
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
		ClientSimulator simulator = new ClientSimulator(a, b)
				.setDepth(depth)
				.setSoftMaxTime(softMaxTime)
				.setMuted(true);
		simulator.run();
		
		int score1 = simulator.getPlayer1().getScore();
		int score2 = simulator.getPlayer2().getScore();
		
		if (score1 > score2) {
			players.put(a, players.get(a) + 1);
		} else if (score1 < score2) {
			players.put(b, players.get(b) + 1);
		}
	}
	
	private void println(String str) {
		if (!muted) {
			System.out.println(str);
		}
	}
	
	public void start() {
		println(" ==== Client Bench ==== ");
		println("");
		
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

	private void simulateAndPrint(Player playerA, Player playerB) {
		simulate(playerA, playerB);
		printScores();
	}
	
	private void printScores() {
		for (Player player : players.keySet()) {
			Integer score = players.get(player);
			
			System.out.print("[" + player.getPlayerName() + ":] " + score.toString() + "\t\t");
		}
		
		System.out.println();
	}
}
