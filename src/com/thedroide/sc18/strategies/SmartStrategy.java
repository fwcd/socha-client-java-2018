package com.thedroide.sc18.strategies;

import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.algorithmics.Strategy;
import com.thedroide.sc18.minimax.MinimaxBoardState;

/**
 * A smarter strategy. Currently the best
 * one available.
 */
public class SmartStrategy implements Strategy<MinimaxBoardState> {
	private Strategy<MinimaxBoardState> baseStrategy = new RaceStrategy(); // Will be used in case all the other checks "fail"
	
	@Override
	public Rating evaluate(MinimaxBoardState move) {
		// TODO
		
		return baseStrategy.evaluate(move);
	}
}
