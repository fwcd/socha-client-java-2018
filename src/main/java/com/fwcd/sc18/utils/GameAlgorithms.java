package com.fwcd.sc18.utils;

import com.fwcd.sc18.evaluator.MoveEvaluator;
import com.fwcd.sc18.evaluator.MovePruner;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

public final class GameAlgorithms {
	private GameAlgorithms() {}

	public static float alphaBeta(
			boolean maximizing,
			Move move,
			GameState gameBeforeMove,
			int depth,
			PlayerColor myColor,
			float alpha,
			float beta,
			MovePruner pruner,
			MoveEvaluator evaluator
	) {
		GameState gameAfterMove;
		try {
			gameAfterMove = HUIUtils.spawnChild(gameBeforeMove, move);
		} catch (InvalidMoveException | InvalidGameStateException e) {
			if (maximizing) {
				return Float.NEGATIVE_INFINITY;
			} else {
				return Float.POSITIVE_INFINITY;
			}
		}
		
		boolean wasPruned = false;
		if (depth <= 0 || HUIUtils.isGameOver(gameAfterMove) || (pruner != null && (wasPruned = pruner.shouldPrune(move, myColor, gameBeforeMove, gameAfterMove)))) {
			return evaluator.rate(move, myColor, gameBeforeMove, gameAfterMove, wasPruned);
		} else {
			float bestRating = maximizing ? alpha : beta;
			
			for (Move childMove : gameAfterMove.getPossibleMoves()) {
				// TODO: Timer?
				float rating;
				
				if (maximizing) {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, myColor, bestRating, beta, pruner, evaluator);
					if (rating > bestRating) {
						bestRating = rating;
						if (bestRating >= beta) {
							break; // Beta-cutoff
						}
					}
				} else {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, myColor, alpha, bestRating, pruner, evaluator);
					if (rating < bestRating) {
						bestRating = rating;
						if (bestRating <= alpha) {
							break; // Alpha-cutoff
						}
					}
				}
			}
			
			return bestRating;
		}
	}
}