package com.thedroide.sc18.choosers;

/**
 * A SimpleClient-based player. Mostly for debugging.
 */
public class SimplePlayer extends BasicPlayer {
	public SimplePlayer() {
		super(new SimpleMoveChooser());
	}
}
