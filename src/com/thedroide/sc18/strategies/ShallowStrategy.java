package com.thedroide.sc18.strategies;

import java.util.Optional;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * Provides a way to filter "obvious" best moves
 * (like salad-fields or goal-moves) quickly before
 * even building a game tree or using minimax. It's
 * called shallow strategy, because it shouldn't predict
 * any moves and is basically the first check when
 * evaluating a board.
 * 
 * @deprecated Use {@link HUIHeuristic} and it's pruneMove() instead.
 */
@Deprecated
public interface ShallowStrategy {
	/**
	 * May or may not calculate a "best move" for
	 * a given game state.
	 * 
	 * @param game - The state (before any move)
	 * @return A "best move"
	 */
	public Optional<Move> bestMove(GameState game);
}
