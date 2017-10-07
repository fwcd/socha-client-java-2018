package com.thedroide.sc18.strategies;

import com.thedroide.sc18.bindings.HUIEnumPlayer;
import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;

/**
 * An API wrapper providing heuristics about
 * leaf moves in the game-tree and more
 * convenient parameters.
 */
public interface LeafHeuristic {
	/**
	 * Decides if a move should be "cut off".
	 * 
	 * @param gameBeforeMove - The game state BEFORE the move
	 * @param move - The move to be evaluated
	 * @param player - The player who committed the move
	 * @return Whether the move should be pruned
	 */
	public boolean pruneMove(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player);
	
	/**
	 * Rates a move on the associated game board.
	 * 
	 * @param gameBeforeMove - The game state BEFORE the move
	 * @param move - The move to be rated
	 * @param player - The player who committed the move
	 * @return A rating of this move
	 */
	public double heuristic(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player);
}
