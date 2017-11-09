package com.thedroide.sc18;

import com.antelmann.game.AutoPlay;
import com.thedroide.sc18.bindings.HUIMove;

public class AIThread implements Runnable {
	private boolean started = false;
	private Thread thread;
	private AutoPlay ai;

	private boolean discarded = false;
	private HUIMove move = null;
	
	public AIThread(AutoPlay ai) {
		this.ai = ai;
	}
	
	public void start() {
		if (!started) {
			thread = new Thread(this, "AIThread");
			thread.start();
		} else {
			throw new IllegalThreadStateException("Can't start an already started AIThread again!");
		}
	}
	
	public void discard() {
		discarded = true;
	}
	
	@Override
	public void run() {
		HUIMove foundMove = (HUIMove) ai.autoMove();
		
		if (!discarded) {
			move = foundMove;
		}
	}
	
	/**
	 * Joins the AI thread.
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
	
	public HUIMove getNullableMove() {
		return move;
	}
}
