package com.thedroide.sc18.alphabeta;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameUtilities;
import com.thedroide.sc18.core.TreeSearchPlayer;
import com.thedroide.sc18.utils.WIP;

@WIP(usable = false)
public class IterativeDeepeningABPlayer extends TreeSearchPlayer {
	@Override
	public String getPlayerName() {
		return "IDABPlayer";
	}

	/**
	 * Performs an iteratively deepening alpha-beta search. It ignores the
	 * provided level/depth and searches until the provided milliseconds have
	 * passed.
	 */
	@Override
	public double evaluate(GamePlay game, GameMove move, int[] role, int level, long ms) {
		long start = System.currentTimeMillis();
		double maxRating = Double.NEGATIVE_INFINITY;
		
		// FIXME: Almost never conforms to the provided (soft) response time
		// limit. This lets the OwnLogic fallback to a shallow strategy
		// which leads to unexpected behavior. Thus this algorithm is "broken"
		// at the moment.
		
		int depth = 0;
		while ((System.currentTimeMillis() - start) < ms) {
			long timeLeft = (System.currentTimeMillis() - start) - ms;
			maxRating = Math.max(
					maxRating,
					GameUtilities.alphaBetaSearch(game, move, this, role, depth, timeLeft, doesOrderMoves())
			);
			depth++;
		}
		
		return maxRating;
	}
}
