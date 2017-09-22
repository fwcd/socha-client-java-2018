package com.thedroide.sc18.strategies;

import com.thedroide.sc18.algorithmics.MoveRating;
import com.thedroide.sc18.algorithmics.Strategy;
import com.thedroide.sc18.minimax.IntRating;

import sc.plugin2018.Action;
import sc.plugin2018.Advance;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * A very simple strategy that focuses
 * on moving forward.
 */
public class RaceStrategy implements Strategy {
	@Override
	public MoveRating evaluate(Move move, GameState state) {
		for (Action action : move.getActions()) {
			if (action instanceof Advance) {
				return new IntRating(((Advance) action).getDistance());
			}
		}
		
		return new IntRating(0);
	}
}
