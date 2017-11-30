package com.thedroide.sc18.strategies;

import java.util.ArrayList;
import java.util.List;

import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;

import sc.player2018.logic.SimpleLogic;
import sc.plugin2018.Action;
import sc.plugin2018.Advance;
import sc.plugin2018.Board;
import sc.plugin2018.Card;
import sc.plugin2018.CardType;
import sc.plugin2018.ExchangeCarrots;
import sc.plugin2018.FallBack;
import sc.plugin2018.FieldType;
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
	public HUIMove bestMove(HUIGamePlay gamePlay) {
		List<HUIMove> possibleMoves = gamePlay.getLegalMovesList();
		List<HUIMove> saladMoves = new ArrayList<>();
		List<HUIMove> selectedMoves = new ArrayList<>();

		Board board = gamePlay.getBoard();
		Player player = gamePlay.nextHUIEnumPlayer().getSCPlayer(gamePlay);
		int index = player.getFieldIndex();
		
		for (HUIMove huiMove : possibleMoves) {
			Move move = huiMove.getSCMove();
			for (Action action : move.getActions()) {
				if (action instanceof Advance) {
					Advance advance = (Advance) action;
					if (advance.getDistance() + index == Constants.NUM_FIELDS - 1) {
						// Return winning move
						return huiMove;
					} else if (board.getTypeAt(advance.getDistance() + index) == FieldType.SALAD) {
						// Remember salad-move
						saladMoves.add(huiMove);
					} else {
						// Otherwise advance if possible
						selectedMoves.add(huiMove);
					}
				} else if (action instanceof Card) {
					if (((Card) action).getType() == CardType.EAT_SALAD) {
						// Remember salad-move
						saladMoves.add(huiMove);
					}
				} else if (action instanceof ExchangeCarrots) {
					ExchangeCarrots exchangeCarrots = (ExchangeCarrots) action;
					if (exchangeCarrots.getValue() == 10
							&& player.getCarrots() < 30
							&& index < 40
							&& !(player.getLastNonSkipAction() instanceof ExchangeCarrots)) {
						// Pick up carrots only when there are too few, the player isn't near the goal
						// and not two times in a row.
						selectedMoves.add(huiMove);
					} else if (exchangeCarrots.getValue() == -10
							&& player.getCarrots() > 30
							&& index >= 40) {
						// Drop carrots only near the goal.
						selectedMoves.add(huiMove);
					}
				} else if (action instanceof FallBack) {
					if (index > 56 // Last salad field
							&& player.getSalads() > 0) {
						// Fall back only at the end when we need to
						// drop salads.
						selectedMoves.add(huiMove);
					} else if (index <= 56 && index - board.getPreviousFieldByType(FieldType.HEDGEHOG, index) < 5) {
						// Be cautious when falling back.
						selectedMoves.add(huiMove);
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
	
	private HUIMove chooseBest(List<HUIMove> moves) {
		return moves.get(0); // For computational efficiency
	}
}
