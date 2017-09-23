package com.thedroide.sc18.strategies;

import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.algorithmics.Strategy;
import com.thedroide.sc18.minimax.IntRating;
import com.thedroide.sc18.minimax.MinimaxBoardState;

/**
 * A very simple strategy that focuses
 * on moving forward.<br><br>
 * 
 * Will most probably not win, because it
 * doesn't event try to commit winning
 * moves.
 */
public class RaceStrategy implements Strategy<MinimaxBoardState> {
	@Override
	public Rating evaluate(MinimaxBoardState move) {
		int field = move.getState().getCurrentPlayer().getFieldIndex() * (move.isMaximizing() ? 1 : -1);
		return new IntRating(field);
	}
}
