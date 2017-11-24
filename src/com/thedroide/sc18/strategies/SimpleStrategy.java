package com.thedroide.sc18.strategies;

import java.util.ArrayList;
import java.util.List;

import sc.player2018.logic.SimpleLogic;
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

/**
 * Horribly messy code providing a somewhat simple
 * strategy to use (as a last resort - to conform
 * time limits). This code is directly taken from
 * {@link SimpleLogic}, which was provided by the
 * Software Challenge API.
 */
public class SimpleStrategy implements ShallowStrategy {
	@Override
	public Move bestMove(GameState game) {
		List<Move> possibleMoves = game.getPossibleMoves();
		List<Move> saladMoves = new ArrayList<>();
		List<Move> selectedMoves = new ArrayList<>();
		
		Player currentPlayer = game.getCurrentPlayer();
		int index = currentPlayer.getFieldIndex();
		
		for (Move move : possibleMoves) {
			for (Action action : move.actions) {
				if (action instanceof Advance) {
					Advance advance = (Advance) action;
					if (advance.getDistance() + index == Constants.NUM_FIELDS - 1) {
						// Return winning move
						return move;
					} else if (game.getBoard().getTypeAt(advance.getDistance() + index) == FieldType.SALAD) {
						// Remember salad-move
						saladMoves.add(move);
					} else {
						// Otherwise advance if possible
						selectedMoves.add(move);
					}
				} else if (action instanceof Card) {
					if (((Card) action).getType() == CardType.EAT_SALAD) {
						// Remember salad-move
						saladMoves.add(move);
					}
				} else if (action instanceof ExchangeCarrots) {
					ExchangeCarrots exchangeCarrots = (ExchangeCarrots) action;
					if (exchangeCarrots.getValue() == 10
							&& currentPlayer.getCarrots() < 30
							&& index < 40
							&& !(currentPlayer.getLastNonSkipAction() instanceof ExchangeCarrots)) {
						// Pick up carrots only when there are too few, the player isn't near the goal
						// and not two times in a row.
						selectedMoves.add(move);
					} else if (exchangeCarrots.getValue() == -10
							&& currentPlayer.getCarrots() > 30
							&& index >= 40) {
						// Drop carrots only near the goal.
						selectedMoves.add(move);
					}
				} else if (action instanceof FallBack) {
					if (index > 56 // Last salad field
							&& currentPlayer.getSalads() > 0) {
						// Fall back only at the end when we need to
						// drop salads.
						selectedMoves.add(move);
					} else if (index <= 56 && index - game.getPreviousFieldByType(FieldType.HEDGEHOG, index) < 5) {
						// Be cautious when falling back.
						selectedMoves.add(move);
					}
				}
			}
		}
		
		if (!saladMoves.isEmpty()) {
			return chooseBest(saladMoves);
		} else if (!selectedMoves.isEmpty()) {
			return chooseBest(selectedMoves);
		} else {
			return chooseBest(possibleMoves);
		}
	}
	
	private Move chooseBest(List<Move> moves) {
		return moves.get(0); // Using computionally efficient 
	}
}
