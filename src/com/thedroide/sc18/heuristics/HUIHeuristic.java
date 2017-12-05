package com.thedroide.sc18.heuristics;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;

/**
 * An API wrapper providing heuristics about
 * moves in the game-tree and more
 * convenient parameters.
 */
public interface HUIHeuristic {
	/**
	 * Decides if a move should be "cut off".
	 * 
	 * @param gameBeforeMove - The game state BEFORE the move
	 * @param move - The move to be evaluated
	 * @param player - The player who committed the move
	 * @return Whether the move should be pruned
	 */
	boolean pruneMove(HUIGameState gameBeforeMove, HUIMove move, HUIPlayerColor player);
	
	/**
	 * Rates a move on the associated game board.
	 * 
	 * @param gameBeforeMove - The game state BEFORE the move
	 * @param move - The move to be rated
	 * @param player - The player who committed the move
	 * @return A rating of this move
	 */
	float heuristic(HUIGameState gameBeforeMove, HUIMove move, HUIPlayerColor player);
}
