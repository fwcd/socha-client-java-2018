package com.thedroide.sc18.heuristics;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;

/**
 * An API wrapper providing heuristics about
 * moves in the game-tree and more
 * convenient parameters.
 */
@FunctionalInterface
public interface HUIHeuristic {
	/**
	 * Rates a move on the associated game board.
	 * 
	 * @param gameBeforeMove - The game state BEFORE the move
	 * @param gameAfterMove - The game state AFTER the move
	 * @param move - The move to be rated
	 * @param player - The player who committed the move
	 * @return A rating of this move
	 */
	double heuristic(HUIGameState gameBeforeMove, HUIGameState gameAfterMove, HUIMove move, HUIPlayerColor player);
	
	default String shortToString() {
		return "";
	}
}
