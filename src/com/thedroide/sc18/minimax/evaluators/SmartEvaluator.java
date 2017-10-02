package com.thedroide.sc18.minimax.evaluators;

import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.minimax.IntRating;
import com.thedroide.sc18.minimax.MinimaxBoardState;

import sc.plugin2018.Field;
import sc.plugin2018.FieldType;
import sc.plugin2018.Player;

/**
 * A smarter strategy. Currently the best
 * one available.
 */
public class SmartEvaluator implements Evaluator<MinimaxBoardState> {
	@Override
	public Rating evaluate(MinimaxBoardState move) {
		if (move.getLastMove() != null) {
			Player player = move.getState().getCurrentPlayer();
			Field destinationField = move.getLastMove().getDestination();
			
			if (destinationField.getType() == FieldType.GOAL) {
				return IntRating.getMax();
			}
			
			final int carrotOptimum = 8;
			int multiplier = 1;
			
			if (destinationField.getType() == FieldType.HEDGEHOG) {
				multiplier = -500;
			}
			
			int advance = player.getFieldIndex() * 3;
			int salads = -player.getSalads() * 30;
			int carrots = -quickPow(player.getCarrots() - carrotOptimum, 2);
			
			int rating = (advance + salads + carrots) * multiplier;
			
			return new IntRating(rating);
		} else {
			return IntRating.getEmpty();
		}
	}
	
	private int quickPow(int a, int b) {
		int res = a;
		
		for (int i=1; i<b; i++) {
			res *= a;
		}
		
		return res;
	}
}
