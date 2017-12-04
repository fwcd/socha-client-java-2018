package com.thedroide.sc18.minmax;

import com.antelmann.game.AutoPlay;
import com.thedroide.sc18.minmax.core.MinmaxMove;

/**
 * A thread (or more precisely a {@link Runnable}) on which
 * the game logic/AI operates on.
 */
public class AIThread implements Runnable {
	private boolean started = false;
	private Thread thread;
	private AutoPlay ai;

	private boolean discarded = false;
	private MinmaxMove move = null;
	
	public AIThread(AutoPlay ai) {
		this.ai = ai;
	}
	
	/**
	 * Starts this AI thread.
	 */
	public void start() {
		if (!started) {
			thread = new Thread(this, "AIThread");
			thread.start();
			started = true;
		} else {
			throw new IllegalThreadStateException("Can't start an already started AIThread again!");
		}
	}
	
	/**
	 * Signalizes that this AI thread should terminate
	 * soon. Please use this method instead of {@code Thread.interrupt()}!
	 */
	public void discard() {
		discarded = true;
	}
	
	@Override
	public void run() {
		MinmaxMove foundMove = (MinmaxMove) ai.autoMove();
		
		if (!discarded) {
			move = foundMove;
		}
	}
	
	/**
	 * Joins this AI thread using a hard time limit.
	 * 
	 * @param millis - The maximum time to wait
	 * @return Whether the thread is dead
	 */
	public boolean join(long millis) {
		try {
			thread.join(millis);
		} catch (InterruptedException e) {}
		
		return !thread.isAlive();
	}
	
	/**
	 * Fetches the move found by the AI.
	 * 
	 * @return The move or null (if the AI didn't find a move in time)
	 */
	public MinmaxMove getNullableMove() {
		return move;
	}
}
