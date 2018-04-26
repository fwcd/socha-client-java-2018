package com.fwcd.sc18.evaluator;

import com.fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.PlayerColor;

public class HeuristicEvaluator implements MoveEvaluator {
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

		if (carrots == 0) {
			return BAD_RATING;
		}

		if (me.inGoal()) {
			return GOOD_RATING - turn;
		}

		float multiplier = 1;
		int saladWeight = 32;
		int turnWeight = 2;
		int fieldWeight = 6;
		int carrotWeight = 1;
		
		if (fieldIndex > HUIUtils.LAST_SALAD_FIELD && carrots > GameRuleLogic.calculateCarrots(64 - fieldIndex)) {
			multiplier = 0.5F;
		}

		float normFieldIndex = HUIUtils.normalize(fieldIndex, 0, 64);
		float normCarrotRating;
		int carrotOptimum = (salads > 0) ? (int) (GameRuleLogic.calculateCarrots(HUIUtils.distToNextSalad(me, gameAfterMove)) * normFieldIndex) + 10 : 4;
		
		if (carrots > carrotOptimum) {
			normCarrotRating = HUIUtils.invertNormalize(carrots, carrotOptimum, HUIUtils.CARROT_THRESHOLD);
		} else {
			normCarrotRating = HUIUtils.normalize(carrots, 0, carrotOptimum);
		}
		
		// Use weighted sum model to compute final rating
		return ((normCarrotRating * carrotWeight)
				+ (HUIUtils.invertNormalize(salads, 0, 5) * saladWeight)
				+ (normFieldIndex * fieldWeight)
				+ (HUIUtils.invertNormalize(turn, 0, 60) * turnWeight)) * multiplier;
	}
}
