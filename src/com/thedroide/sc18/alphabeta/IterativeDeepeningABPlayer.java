package com.thedroide.sc18.alphabeta;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameUtilities;
import com.thedroide.sc18.core.TreeSearchPlayer;

public class IterativeDeepeningABPlayer extends TreeSearchPlayer {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	
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
		long finishTime = System.currentTimeMillis() + ms;
		double maxRating = Double.NEGATIVE_INFINITY;
		
		int depth = 0;
		while (System.currentTimeMillis() < finishTime) {
			final int localDepth = depth;
			Future<Double> rating = threadPool.submit(() -> GameUtilities.alphaBetaSearch(
					game,
					move,
					this,
					role,
					localDepth,
					finishTime,
					doesOrderMoves()
			));
			long remaining = finishTime - System.currentTimeMillis();
			
			try {
				maxRating = Math.max(maxRating, rating.get(remaining, TimeUnit.MILLISECONDS));
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				break; // Break the loop if anything takes too long/goes wrong (this will almost always be the case)
			}
			depth++;
		}
		
		LOG.debug("Iterative alpha-beta reached {} whole levels...", depth - 1);
		
		return maxRating;
	}
}
