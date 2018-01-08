package com.thedroide.sc18.heuristics;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;

@FunctionalInterface
public interface HUIPruner {
	/**
	 * Decides whether a move should be "cut off".
	 * 
	 * @param gameBeforeMove - The game state BEFORE the move
	 * @param gameAfterMove - The game state AFTER the move
	 * @param move - The move to be evaluated
	 * @param player - The player who committed the move
	 * @return Whether the move should be pruned
	 */
	boolean pruneMove(HUIGameState gameBeforeMove, HUIGameState gameAfterMove,  HUIMove move, HUIPlayerColor player);
}
