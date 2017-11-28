package com.thedroide.sc18.strategies;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * <b>Quickly</b> calculates a move from a game state (as a last
 * resort to conform time limits) using simpler logic.<br><br>
 * 
 * It's called shallow strategy, because it
 * shouldn't deeply predict
 * any moves and is basically the first check when
 * evaluating a board.
 */
public interface ShallowStrategy {
	/**
	 * Quickly calculates a "best move" for
	 * a given game state.
	 * 
	 * @param game - The state (before any move)
	 * @return A good move
	 */
	Move bestMove(GameState game);
}
