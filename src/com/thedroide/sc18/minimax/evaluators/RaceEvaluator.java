package com.thedroide.sc18.minimax.evaluators;

import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.algorithmics.Evaluator;
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
public class RaceEvaluator implements Evaluator<MinimaxBoardState> {
	@Override
	public Rating evaluate(MinimaxBoardState move) {
		int field = move.getState().getCurrentPlayer().getFieldIndex();
		return new IntRating(field);
	}
}
