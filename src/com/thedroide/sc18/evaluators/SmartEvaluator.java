package com.thedroide.sc18.evaluators;

import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.utils.IntRating;

public class SmartEvaluator implements Evaluator {
	@Override
	public Rating evaluate(AMove move, ABoardState state) {
		// TODO: Implement this properly
		return new IntRating(state.getCurrentPlayer().getCarrots());
	}
}
