package com.fwcd.sc18.core;

import com.fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.InvalidMoveException;

public class AlphaBetaLogic extends EvaluatingLogic {
	private static final float LARGE_NUMBER = 10000000000000F;
	
	private float fieldWeight = 128;
	private float carrotWeight = 2;
	private float saladWeight = 256;
	private float turnWeight = 2;
	private int alphaBetaDepth = 4;
	
	public AlphaBetaLogic(AbstractClient client) {
		super(client);
	}

	private float evaluateLeaf(boolean maximizing, Move move, GameState gameBeforeMove, GameState gameAfterMove) {
		Player me = getMe(gameAfterMove);
		Player opponent = getOpponent(gameAfterMove);
		float myRating = 0;
		
		if (me.inGoal()) {
			myRating = LARGE_NUMBER;
		} else if (opponent.inGoal()) {
			myRating = -LARGE_NUMBER; 
		} else {
			myRating += HUIUtils.invertNormalize(me.getSalads(), 0, 5) * saladWeight;
			myRating += HUIUtils.invertNormalize(me.getCarrots(), 0, 200) * carrotWeight;
			myRating += HUIUtils.normalize(me.getFieldIndex(), 0, 64) * fieldWeight;
			myRating += HUIUtils.invertNormalize(gameAfterMove.getTurn(), 0, 60) * turnWeight;
		}
		
		return maximizing ? myRating : -myRating;
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
		} catch (InvalidMoveException e) {
			if (maximizing) {
				return Float.NEGATIVE_INFINITY;
			} else {
				return Float.POSITIVE_INFINITY;
			}
		}
		
		if (depth <= 0 || HUIUtils.isGameOver(gameAfterMove)) {
			return evaluateLeaf(maximizing, move, gameBeforeMove, gameAfterMove);
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
	
	@Override
	protected float evaluateMove(Move move, GameState gameBeforeMove, Player me) {
		return alphaBeta(true, move, gameBeforeMove, alphaBetaDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	@Override
	public CopyableLogic copy(AbstractClient client) {
		return new AlphaBetaLogic(client);
	}
}
