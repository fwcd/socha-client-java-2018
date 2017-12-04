package com.thedroide.sc18.mcts.choosers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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

public class SimpleMoveChooser implements MCTSMoveChooser {
	private static final Random RANDOM = ThreadLocalRandom.current();
	
	@Override
	public Move chooseMove(GameState state, Player currentPlayer) {
		List<Move> possibleMove = state.getPossibleMoves(); // Mindestens ein element
		List<Move> saladMoves = new ArrayList<>();
		List<Move> winningMoves = new ArrayList<>();
		List<Move> selectedMoves = new ArrayList<>();

		int index = currentPlayer.getFieldIndex();
		for (Move move : possibleMove) {
			for (Action action : move.actions) {
				if (action instanceof Advance) {
					Advance advance = (Advance) action;
					if (advance.getDistance() + index == Constants.NUM_FIELDS - 1) {
						// Zug ins Ziel
						winningMoves.add(move);

					} else if (state.getBoard().getTypeAt(advance.getDistance() + index) == FieldType.SALAD) {
						// Zug auf Salatfeld
						saladMoves.add(move);
					} else {
						// Ziehe Vorwaerts, wenn moeglich
						selectedMoves.add(move);
					}
				} else if (action instanceof Card) {
					Card card = (Card) action;
					if (card.getType() == CardType.EAT_SALAD) {
						// Zug auf Hasenfeld und danch Salatkarte
						saladMoves.add(move);
					} // Muss nicht zusätzlich ausgewählt werden, wurde schon
						// durch Advance ausgewaehlt
				} else if (action instanceof ExchangeCarrots) {
					ExchangeCarrots exchangeCarrots = (ExchangeCarrots) action;
					if (exchangeCarrots.getValue() == 10 && currentPlayer.getCarrots() < 30 && index < 40
							&& !(currentPlayer.getLastNonSkipAction() instanceof ExchangeCarrots)) {
						// Nehme nur Karotten auf, wenn weniger als 30 und nur
						// am Anfang und nicht zwei mal hintereinander
						selectedMoves.add(move);
					} else if (exchangeCarrots.getValue() == -10 && currentPlayer.getCarrots() > 30 && index >= 40) {
						// abgeben von Karotten ist nur am Ende sinnvoll
						selectedMoves.add(move);
					}
				} else if (action instanceof FallBack) {
					if (index > 56 /* letztes Salatfeld */ && currentPlayer.getSalads() > 0) {
						// Falle nur am Ende (index > 56) zurueck, ausser du
						// musst noch einen Salat loswerden
						selectedMoves.add(move);
					} else if (index <= 56 && index - state.getPreviousFieldByType(FieldType.HEDGEHOG, index) < 5) {
						// Falle zuruek, falls sich Rueckzug lohnt (nicht zu
						// viele Karotten aufnehmen)
						selectedMoves.add(move);
					}
				} else {
					// Fuege Salatessen oder Skip hinzu
					selectedMoves.add(move);
				}
			}
		}

		Move move;
		
		if (!winningMoves.isEmpty()) {
			move = winningMoves.get(RANDOM.nextInt(winningMoves.size()));
		} else if (!saladMoves.isEmpty()) {
			move = saladMoves.get(RANDOM.nextInt(saladMoves.size()));
		} else if (!selectedMoves.isEmpty()) {
			move = selectedMoves.get(RANDOM.nextInt(selectedMoves.size()));
		} else {
			move = possibleMove.get(RANDOM.nextInt(possibleMove.size()));
		}

		return move;
	}
}
