package com.thedroide.sc18.mcts;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;
import com.thedroide.sc18.core.ScratchPlayer;
import com.thedroide.sc18.core.TreeSearchPlayer;

/**
 * A player using "monte-carlo-tree-search". This is intentionally
 * not a {@link TreeSearchPlayer}, because it doesn't need a speficied
 * heuristic.
 */
public class MCTSPlayer extends ScratchPlayer {
	@Override
	public String getPlayerName() {
		return "MCTSPlayer";
	}

	@Override
	protected HUIMove pickMove(HUIGameState game, HUIPlayerColor player, int depth, long ms) {
		long start = System.currentTimeMillis();
		MCTSNode node = new MCTSNode(player, game);
		
		while ((System.currentTimeMillis() - start) < ms) {
			node.performIteration();
		}
		
		return node.mostExploredChild().getMove();
	}
}
