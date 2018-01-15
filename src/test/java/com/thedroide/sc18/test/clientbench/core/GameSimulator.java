package com.thedroide.sc18.test.clientbench.core;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.antelmann.game.AutoPlay;
import com.antelmann.game.Player;
import com.thedroide.sc18.core.HUIDriver;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;

import sc.plugin2018.GameState;

public class GameSimulator implements Runnable {
	private static final int TURNS = 60;
	private boolean muted = true;
	
	private final VirtualPlayer p1;
	private final VirtualPlayer p2;

	private int gameRounds = 5;
	private int threadCount = 1;
	private ExecutorService pool = Executors.newSingleThreadExecutor();

	private long softMaxTime = Long.MAX_VALUE;
	private int depth = 2;
	private Optional<GameView> boundView = Optional.empty();
	
	public GameSimulator(Player strategy1, Player strategy2) {
		GameState state = new GameState();
		
		p1 = new VirtualPlayer(strategy1);
		p1.setColor(state.getStartPlayerColor());
		p2 = new VirtualPlayer(strategy2);
		p2.setColor(state.getStartPlayerColor().opponent());
	}
	
	/**
	 * Sets whether this simulator should
	 * log to the console or not.
	 * 
	 * @param muted - Whether this simulator should be "muted"
	 */
	public GameSimulator setMuted(boolean muted) {
		this.muted = muted;
		return this;
	}
	
	public GameSimulator setDepth(int depth) {
		this.depth = depth;
		return this;
	}
	
	public GameSimulator setSoftMaxTime(long ms) {
		softMaxTime = ms;
		return this;
	}
	
	public GameSimulator setThreadCount(int threadCount) {
		this.threadCount = threadCount;
		pool = Executors.newFixedThreadPool(threadCount);
		return this;
	}
	
	public GameSimulator setGameRounds(int rounds) {
		gameRounds = rounds;
		return this;
	}
	
	public GameSimulator bind(GameView view) {
		boundView = Optional.of(view);
		return this;
	}
	
	public VirtualPlayer getPlayer1() {
		return p1;
	}
	
	public VirtualPlayer getPlayer2() {
		return p2;
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
	
	public void resetScores() {
		p1.resetScore();
		p2.resetScore();
	}
	
	private VirtualPlayer getWinner(HUIGameState game) {
		int field1 = game.getSCPlayer(p1.getHUIPlayerColor()).getFieldIndex();
		int field2 = game.getSCPlayer(p2.getHUIPlayerColor()).getFieldIndex();
		
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
	
	/**
	 * Starts this client simulator using the given number of
	 * threads.
	 */
	public void start() {
		println(" ==== Client Simulator ==== ");
		println(" ~~ [" + Integer.toString(gameRounds) + " rounds] - [" + Integer.toString(threadCount) + " threads] ~~");
		println(" ~~�[" + p1.getName() + "] vs [" + p2.getName() + "] ~~ ");
		println("");
		
		for (int i=0; i<gameRounds; i++) {
			pool.execute(this);
		}
		
		pool.shutdown();
	}

	private void println(String str) {
		if (!muted) {
			System.out.println(str);
		}
	}
	
	/**
	 * Executes this client simulator once in the current thread.
	 */
	@Override
	public void run() {
		HUIGameState game = new HUIGameState(new GameState());
		VirtualPlayer current = p1;
		HUIDriver autoPlay = new HUIDriver(game, depth, p1.getAI(), p2.getAI());
		
		autoPlay.setResponseTime(softMaxTime);
		game.getSCPlayer(HUIPlayerColor.RED).setDisplayName(p1.getName());
		game.getSCPlayer(HUIPlayerColor.BLUE).setDisplayName(p2.getName());
		
		int t = 0;
		while (t < TURNS && game.getWinner() == null) {
			if (boundView.isPresent()) {
				boundView.orElse(null).update(game);
			}
			
			autoPlay.setGame(game);
			HUIMove move = autoPlay.hint(game.nextPlayer());
			game = game.spawnChild(move);
			current = opponentOf(current);
			
			t++;
		}
		
		updateScores(autoPlay, game);
		
		println(
				Integer.toString(p1.getScore())
				+ "\t:\t"
				+ Integer.toString(p2.getScore())
				+ "\t[Thread "
				+ Integer.toHexString(Thread.currentThread().hashCode())
				+ "]"
		);
	}
}
