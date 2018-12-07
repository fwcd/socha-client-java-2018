package fwcd.sc18.evaluator;

import java.util.Random;

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
	private static final float WINNING_MOVE = 100;
	private static final float SALAD_MOVE = 80;
	private static final float SELECTED_MOVE = 50;
	private static final float OTHER_MOVE = 0;
	private static final Random RANDOM = new Random();

	@Override
	public float rate(Move move, PlayerColor myColor, GameState gameBeforeMove, GameState gameAfterMove, boolean wasPruned) {
		Player currentPlayer = gameBeforeMove.getPlayer(myColor);
		int index = currentPlayer.getFieldIndex();
		float initialRating = OTHER_MOVE;

		for (Action action : move.actions) {
			if (action instanceof Advance) {
				Advance advance = (Advance) action;
				if (advance.getDistance() + index == Constants.NUM_FIELDS - 1) {
					// Enter goal
					initialRating = WINNING_MOVE;

				} else if (gameBeforeMove.getBoard().getTypeAt(advance.getDistance() + index) == FieldType.SALAD) {
					// Move to salad field
					initialRating = SALAD_MOVE;
				} else {
					// Advance if possible
					initialRating = SELECTED_MOVE;
				}
			} else if (action instanceof Card) {
				Card card = (Card) action;
				if (card.getType() == CardType.EAT_SALAD) {
					// Advance to hare field and eat salad
					initialRating = SALAD_MOVE;
				}
			} else if (action instanceof ExchangeCarrots) {
				ExchangeCarrots exchangeCarrots = (ExchangeCarrots) action;
				if (exchangeCarrots.getValue() == 10 && currentPlayer.getCarrots() < 30 && index < 40
						&& !(currentPlayer.getLastNonSkipAction() instanceof ExchangeCarrots)) {
					// Only pick up carrots if there are less than 30,
					// just at the beginning and not twice in a row
					initialRating = SELECTED_MOVE;
				} else if (exchangeCarrots.getValue() == -10 && currentPlayer.getCarrots() > 30 && index >= 40) {
					// Only drop carrots if at the end
					initialRating = SELECTED_MOVE;
				}
			} else if (action instanceof FallBack) {
				if (index > 56 /* Last salad field */ && currentPlayer.getSalads() > 0) {
					// Just fall back at the end if we still have salads
					initialRating = SELECTED_MOVE;
				} else if (index <= 56 && index - gameBeforeMove.getPreviousFieldByType(FieldType.HEDGEHOG, index) < 5) {
					// Don't pick up too many carrots while falling back
					initialRating = SELECTED_MOVE;
				}
			} else {
				// Add "eat salad" or "skip"
				initialRating = SELECTED_MOVE;
			}
		}

		// TODO: Performance?
		return initialRating * (float) RANDOM.nextGaussian();
	}
}
