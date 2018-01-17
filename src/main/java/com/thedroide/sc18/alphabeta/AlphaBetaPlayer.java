package com.thedroide.sc18.alphabeta;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameUtilities;
import com.thedroide.sc18.core.TreeSearchPlayer;

/**
 * Represents a minimax-alpha-beta player.
 */
public class AlphaBetaPlayer extends TreeSearchPlayer {
	@Override
	public double evaluate(GamePlay game, GameMove move, int[] role, int level, long ms) {
		return GameUtilities.alphaBetaSearch(game, move, this, role, level, System.currentTimeMillis() + ms, doesOrderMoves());
	}
}
