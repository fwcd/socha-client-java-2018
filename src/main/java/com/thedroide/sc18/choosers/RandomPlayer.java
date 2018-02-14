package com.thedroide.sc18.choosers;

/**
 * A SimpleClient-based player. Mostly for debugging.
 */
public class RandomPlayer extends BasicPlayer {
	public RandomPlayer() {
		super(new RandomMoveChooser());
	}
}
