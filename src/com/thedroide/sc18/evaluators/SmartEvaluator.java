package com.thedroide.sc18.evaluators;

import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;
import com.thedroide.sc18.algorithmics.APlayer;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.utils.IntRating;

public class SmartEvaluator implements Evaluator {
	private final int carrotOptimum = 8;
	
	@Override
	public Rating evaluate(AMove move, ABoardState state) {
		APlayer player = state.getCurrentPlayer();
		
		int field = state.getCurrentPlayer().getField().getIndex();
		int carrots = -square(player.getCarrots() - carrotOptimum) * 16;
		int salads = -player.getSalads() * 32;
		
		int rating = carrots + salads + field;
		
		return new IntRating(rating);
	}
	
	private int square(int n) {
		return n * n;
	}
}
