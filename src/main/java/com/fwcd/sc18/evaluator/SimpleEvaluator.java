package com.fwcd.sc18.evaluator;

import java.util.ArrayList;
import java.util.List;

import sc.plugin2018.Action;
import sc.plugin2018.Advance;
import sc.plugin2018.Card;
import sc.plugin2018.CardType;
import sc.plugin2018.ExchangeCarrots;
import sc.plugin2018.FallBack;
import sc.plugin2018.FieldType;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.plugin2018.util.Constants;
import sc.shared.PlayerColor;

/**
 * A simple MoveEvaluator that build upon the
 * SimpleClient logic.
 */
public class SimpleEvaluator implements MoveEvaluator {
	public static final float WINNING_MOVE = 100;
	public static final float SALAD_MOVE = 80;
	public static final float SELECTED_MOVE = 50;
	public static final float OTHER_MOVE = 0;

	@Override
	public float rate(Move move, PlayerColor myColor, GameState gameBeforeMove, GameState gameAfterMove, boolean wasPruned) {
		Player currentPlayer = gameBeforeMove.getPlayer(myColor);
		int index = currentPlayer.getFieldIndex();
		for (Action action : move.actions) {
			if (action instanceof Advance) {
				Advance advance = (Advance) action;
				if (advance.getDistance() + index == Constants.NUM_FIELDS - 1) {
					// Enter goal
					return WINNING_MOVE;

				} else if (gameBeforeMove.getBoard().getTypeAt(advance.getDistance() + index) == FieldType.SALAD) {
					// Move to salad field
					return SALAD_MOVE;
				} else {
					// Advance if possible
					return SELECTED_MOVE;
				}
			} else if (action instanceof Card) {
				Card card = (Card) action;
				if (card.getType() == CardType.EAT_SALAD) {
					// Advance to hare field and eat salad
					return SALAD_MOVE;
				}
			} else if (action instanceof ExchangeCarrots) {
				ExchangeCarrots exchangeCarrots = (ExchangeCarrots) action;
				if (exchangeCarrots.getValue() == 10 && currentPlayer.getCarrots() < 30 && index < 40
						&& !(currentPlayer.getLastNonSkipAction() instanceof ExchangeCarrots)) {
					// Only pick up carrots if there are less than 30,
					// just at the beginning and not twice in a row
					return SELECTED_MOVE;
				} else if (exchangeCarrots.getValue() == -10 && currentPlayer.getCarrots() > 30 && index >= 40) {
					// Only drop carrots if at the end
					return SELECTED_MOVE;
				}
			} else if (action instanceof FallBack) {
				if (index > 56 /* Last salad field */ && currentPlayer.getSalads() > 0) {
					// Just fall back at the end if we still have salads
					return SELECTED_MOVE;
				} else if (index <= 56 && index - gameBeforeMove.getPreviousFieldByType(FieldType.HEDGEHOG, index) < 5) {
					// Don't pick up too many carrots while falling back
					return SELECTED_MOVE;
				}
			} else {
				// Add "eat salad" or "skip"
				return SELECTED_MOVE;
			}
		}

		return OTHER_MOVE;
	}
}