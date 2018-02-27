package com.fwcd.sc18.core;

import com.fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;

public class AlphaBetaLogic extends EvaluatingLogic {
	private int depth = 4;
	
	public AlphaBetaLogic(AbstractClient client) {
		super(client);
	}

	@Override
	public CopyableLogic copy(AbstractClient client) {
		return new AlphaBetaLogic(client);
	}

	@Override
	protected float evaluateMove(Move move, GameState gameBeforeMove, Player me) {
		return alphaBeta(false, move, gameBeforeMove, depth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	private float evaluateLeaf(Move move, GameState gameBeforeMove, GameState gameAfterMove) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private float alphaBeta(
			boolean maximizing,
			Move move,
			GameState gameBeforeMove,
			int depth,
			float alpha,
			float beta
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
		
		if (depth <= 0 || HUIUtils.isGameOver(gameAfterMove)) {
			return evaluateLeaf(move, gameBeforeMove, gameAfterMove);
		} else {
			float bestRating = maximizing ? alpha : beta;
			
			for (Move childMove : gameAfterMove.getPossibleMoves()) {
				// TODO: Timer?
				float rating;
				
				if (maximizing) {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, bestRating, beta);
					if (rating > bestRating) {
						bestRating = rating;
						if (bestRating >= beta) {
							break; // Beta-cutoff
						}
					}
				} else {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, alpha, bestRating);
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
