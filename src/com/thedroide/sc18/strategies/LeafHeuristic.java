package com.thedroide.sc18.strategies;

import com.thedroide.sc18.bindings.HUIEnumPlayer;
import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;

/**
 * Provides hopefully accuate heuristics about
 * leaf moves in the game-tree.
 */
public interface LeafHeuristic {
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
