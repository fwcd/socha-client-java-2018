package com.fwcd.sc18.alphabeta;

import com.fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.PlayerColor;

public class MoveEvaluator {
	public static final float GOOD_RATING = 10000000;
	public static final float BAD_RATING = -10000000;

	public float rate(Move move, PlayerColor myColor, GameState gameBeforeMove, GameState gameAfterMove, boolean wasPruned) {
		if (wasPruned) {
			return BAD_RATING;
		}

		Player me = gameAfterMove.getPlayer(myColor);
		int fieldIndex = me.getFieldIndex();
		int carrots = me.getCarrots();
		int salads = me.getSalads();
		int turn = gameAfterMove.getTurn();

		if (me.inGoal()) {
			return GOOD_RATING - turn;
		}

		int saladWeight = 32;
		int turnWeight = 1;
		int fieldWeight = 4;
		int carrotWeight = 1;
		
		float normCarrotRating;
		int carrotOptimum = (salads > 0) ? 40 : 4;
		int carrotsToNextSalad = GameRuleLogic.calculateCarrots(HUIUtils.distToNextSalad(me, gameAfterMove));
		
		if (shouldCollectCarrots(me, carrotsToNextSalad)) {
			carrotWeight *= 8;
			normCarrotRating = HUIUtils.normalize(carrots, 0, carrotsToNextSalad);
		} else if (carrots > carrotOptimum) {
			normCarrotRating = HUIUtils.invertNormalize(carrots, carrotOptimum, carrotOptimum * 2);
		} else {
			normCarrotRating = HUIUtils.normalize(carrots, 0, carrotOptimum);
		}
		
		// Use weighted sum model to compute final rating
		return (normCarrotRating * carrotWeight)
				+ (HUIUtils.invertNormalize(salads, 0, 5) * saladWeight)
				+ (HUIUtils.normalize(fieldIndex, 0, 64) * fieldWeight)
				+ (HUIUtils.invertNormalize(turn, 0, 60) * turnWeight);
	}

	private boolean shouldCollectCarrots(Player me, int carrotsToNextSalad) {
		boolean needsToDropSalads = me.getSalads() > 0;
		boolean insufficientCarrotsToNextSalad = me.getCarrots() < carrotsToNextSalad;
		
		return needsToDropSalads && insufficientCarrotsToNextSalad;
	}
}
