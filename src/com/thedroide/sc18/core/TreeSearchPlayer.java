package com.thedroide.sc18.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.Player;
import com.antelmann.game.TemplatePlayer;
import com.thedroide.sc18.heuristics.HUIHeuristic;
import com.thedroide.sc18.heuristics.SmartHeuristic;

/**
 * This class aims to provide a better (domain-specific) base
 * implementation for deterministic tree-search/evaluation-based algorithms
 * than {@link TemplatePlayer}.
 */
public abstract class TreeSearchPlayer implements Player {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	private HUIHeuristic heuristic = new SmartHeuristic();
	private boolean orderMoves = false;
	
	public void setHeuristic(HUIHeuristic heuristic) {
		this.heuristic = Objects.requireNonNull(heuristic);
	}

	public boolean doesOrderMoves() {
		return orderMoves;
	}
	
	public void setOrderMoves(boolean orderMoves) {
		this.orderMoves = orderMoves;
	}
	
	@Override
	public boolean canPlayGame(GamePlay game) {
		return game instanceof HUIGameState;
	}

	@Override
	public GameMove selectMove(GamePlay game, int[] role, int level, long ms) {
		long finishTime = System.currentTimeMillis() + ms;
		LOG.trace("TreeSearchPlayer selecting a move...");
		HUIGameState huiGame = (HUIGameState) game;
		
		HUIMove bestMove = null;
		double maxRating = Double.NEGATIVE_INFINITY;
		
		List<HUIMove> legalMoves = new ArrayList<>(huiGame.getLegalMovesList());
		long branchMs = ms / legalMoves.size();
		
		for (HUIMove move : legalMoves) {
			LOG.trace("Evaluate move {} with depth {}", move, level);
			double rating = evaluate(game, move, role, level, branchMs);
			
			if (rating > maxRating) {
				bestMove = move;
				maxRating = rating;
			}
			
			long remainingMs = finishTime - System.currentTimeMillis();
			if (Thread.interrupted() || remainingMs <= 0) {
				break; // Break loop if time has run out or the thread was interrupted
			}
		}
		
		return bestMove;
	}

	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role) {
		return heuristic.heuristic(
				(HUIGameState) game,
				(HUIGameState) game.spawnChild(move),
				(HUIMove) move,
				HUIPlayerColor.of(role)
		);
	}

	@Override
	public boolean pruneMove(GamePlay game, GameMove move, int[] role) {
		return heuristic.pruneMove(
				(HUIGameState) game,
				(HUIGameState) game.spawnChild(move),
				(HUIMove) move,
				HUIPlayerColor.of(role)
		);
	}
}
