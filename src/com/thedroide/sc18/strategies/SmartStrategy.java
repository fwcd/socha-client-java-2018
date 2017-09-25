package com.thedroide.sc18.strategies;

import java.util.HashMap;
import java.util.Map;

import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.algorithmics.Strategy;
import com.thedroide.sc18.minimax.IntRating;
import com.thedroide.sc18.minimax.MinimaxBoardState;

import sc.plugin2018.Field;
import sc.plugin2018.FieldType;

/**
 * A smarter strategy. Currently the best
 * one available.
 */
public class SmartStrategy implements Strategy<MinimaxBoardState> {
	private Strategy<MinimaxBoardState> baseStrategy = new RaceStrategy(); // Will be used in case all the other checks "fail"
	private Map<FieldType, Integer> priorizedFields = new HashMap<>();
	
	public SmartStrategy() {
		priorizedFields.put(FieldType.GOAL, Integer.MAX_VALUE);
		priorizedFields.put(FieldType.SALAD, 1000);
	}
	
	@Override
	public Rating evaluate(MinimaxBoardState move) {
		Field destinationField = move.getLastMove().getDestination();
		
		for (FieldType priorizedType : priorizedFields.keySet()) {
			if (destinationField.getType() == priorizedType) {
				return new IntRating(priorizedFields.get(priorizedType));
			}
		}
		
		return baseStrategy.evaluate(move);
	}
}
