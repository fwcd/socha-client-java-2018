package com.thedroide.sc18.strategies;

import com.thedroide.sc18.bindings.HUIEnumPlayer;
import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;

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
	boolean pruneMove(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player);
	
	/**
	 * Rates a move on the associated game board.
	 * 
	 * @param gameBeforeMove - The game state BEFORE the move
	 * @param move - The move to be rated
	 * @param player - The player who committed the move
	 * @return A rating of this move
	 */
	float heuristic(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player);
	
	/**
	 * Provides a quick heuristic mainly used to pre-sort
	 * moves before moving deeper into the tree.
	 * 
	 * @param gameBeforeMove - The game before the move
	 * @param move - The move to be rated
	 * @param player - The player who committed the move
	 * @return A (preferrably) quick rating of this move.
	 */
	float quickHeuristic(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player);
}
