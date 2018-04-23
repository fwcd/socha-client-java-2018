package com.fwcd.sc18.alphabeta;

import com.fwcd.sc18.core.CopyableLogic;
import com.fwcd.sc18.core.EvaluatingLogic;
import com.fwcd.sc18.trainer.core.VirtualClient;
import com.fwcd.sc18.utils.HUIUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

public class AlphaBetaLogic extends EvaluatingLogic {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	private int depth = 4;
	private MoveEvaluator evaluator = new MoveEvaluator();
	private MovePruner pruner = new MovePruner();

	private boolean benchmark = true;
	private int gameStateEvaluations = 0;
	
	public AlphaBetaLogic(VirtualClient client) {
		super(client);
	}
	
	public AlphaBetaLogic(AbstractClient client) {
		super(client);
	}

	@Override
	public CopyableLogic copy(AbstractClient client) {
		return new AlphaBetaLogic(client);
	}

	@Override
	protected float evaluateMove(Move move, GameState gameBeforeMove, Player me) {
		long startTime = System.currentTimeMillis();
		float rating = alphaBeta(false, move, gameBeforeMove, depth, me.getPlayerColor(), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		long delta = System.currentTimeMillis() - startTime;

		if (benchmark) {
			LOG.info("Alpha-Beta Search evaluated {} game states per second", (gameStateEvaluations * 1000) / delta);
			gameStateEvaluations = 0;
		}

		return rating;
	}
	
	private float alphaBeta(
			boolean maximizing,
			Move move,
			GameState gameBeforeMove,
			int depth,
			PlayerColor myColor,
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
		
		boolean wasPruned = false;
		if (depth <= 0 || HUIUtils.isGameOver(gameAfterMove) || (wasPruned = pruner.shouldPrune(move, myColor, gameBeforeMove, gameAfterMove))) {
			if (benchmark) {
				gameStateEvaluations++;
			}

			return evaluator.rate(move, myColor, gameBeforeMove, gameAfterMove, wasPruned);
		} else {
			float bestRating = maximizing ? alpha : beta;
			
			for (Move childMove : gameAfterMove.getPossibleMoves()) {
				// TODO: Timer?
				float rating;
				
				if (maximizing) {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, myColor, bestRating, beta);
					if (rating > bestRating) {
						bestRating = rating;
						if (bestRating >= beta) {
							break; // Beta-cutoff
						}
					}
				} else {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, myColor, alpha, bestRating);
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
