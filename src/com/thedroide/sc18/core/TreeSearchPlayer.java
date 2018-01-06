package com.thedroide.sc18.core;

import java.util.ArrayList;
import java.util.Objects;

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
		HUIGameState huiGame = (HUIGameState) game;
		
		HUIMove bestMove = null;
		double maxRating = Double.NEGATIVE_INFINITY;
		
		for (HUIMove move : new ArrayList<>(huiGame.getLegalMovesList())) {
			// TODO: Parallelisation might be a good idea
			double rating = evaluate(game, move, role, level, ms);
			
			if (rating > maxRating) {
				bestMove = move;
				maxRating = rating;
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
