package com.thedroide.sc18.algorithmics;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * A "Strategy" that will evaluate a certain move.
 * 
 * @param <T> - Rating type
 */
public interface Strategy {
	/**
	 * Evaluates a move.
	 * 
	 * @param move - The move
	 * @param state - The state AFTER the move has been executed (should be cloned before writing to it though)
	 * @return The rating of that move
	 */
	public MoveRating evaluate(Move move, GameState state);
}
