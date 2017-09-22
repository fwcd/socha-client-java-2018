package com.thedroide.sc18.strategies;

import com.thedroide.sc18.algorithmics.MoveRating;
import com.thedroide.sc18.algorithmics.Strategy;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * A smarter strategy. Currently the best
 * one available.
 */
public class SmartStrategy implements Strategy {
	private Strategy baseStrategy = new RaceStrategy(); // Will be used in case all the other checks "fail"
	
	@Override
	public MoveRating evaluate(Move move, GameState state, boolean maximize) {
		// TODO
		
		return baseStrategy.evaluate(move, state, maximize);
	}
}
