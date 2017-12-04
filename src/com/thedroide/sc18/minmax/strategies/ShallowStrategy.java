package com.thedroide.sc18.minmax.strategies;

import com.thedroide.sc18.minmax.core.MinmaxGamePlay;
import com.thedroide.sc18.minmax.core.MinmaxMove;

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
	MinmaxMove bestMove(MinmaxGamePlay game);
}
