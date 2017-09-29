package com.thedroide.sc18.algorithmics;

/**
 * An evaluator that rates a move/board state.<br><br>
 * 
 * May be used inside of a larger algorithm.
 */
public interface Evaluator {
	/**
	 * Evaluates a move (how good this move would be
	 * for the CURRENT player).
	 * 
	 * @param move - The move (including the board state)
	 * @param state - The state AFTER the move has been executed but with the current player set to the person who committed the move
	 * @return The rating of that move
	 */
	public Rating evaluate(AMove move, ABoardState state);
}
