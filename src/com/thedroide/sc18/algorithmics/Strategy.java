package com.thedroide.sc18.algorithmics;

/**
 * An evaluator.
 * 
 * @param <T> - Move type
 */
public interface Strategy<T> {
	/**
	 * Evaluates a move.
	 * 
	 * @param move - The move
	 * @param state - The state AFTER the move has been executed (should be cloned before writing to it though)
	 * @return The rating of that move
	 */
	public Rating evaluate(T move);
}
