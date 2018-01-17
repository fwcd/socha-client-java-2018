package com.thedroide.sc18.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.Player;
import com.antelmann.game.TemplatePlayer;
import com.thedroide.sc18.heuristics.HUIHeuristic;
import com.thedroide.sc18.heuristics.HUIPruner;
import com.thedroide.sc18.heuristics.LightPruner;
import com.thedroide.sc18.heuristics.StatsHeuristic;
import com.thedroide.sc18.utils.Lazy;

/**
 * This class aims to provide a better (domain-specific) base
 * implementation for deterministic tree-search/evaluation-based algorithms
 * than {@link TemplatePlayer}.
 */
public abstract class TreeSearchPlayer implements Player {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	
	private HUIHeuristic heuristic = new StatsHeuristic();
	private HUIPruner pruner = new LightPruner();
	private boolean orderMoves = false;
	private Lazy<String> stringRepresentation = new Lazy<>(() -> getClass().getSimpleName() + ": " + heuristic.shortToString());
	
	/**
	 * Small implementation data-class. 
	 */
	private static class BestResult {
		private volatile HUIMove bestMove = null;
		private volatile double bestRating = Double.NEGATIVE_INFINITY;
	}

	@Override
	public String getPlayerName() {
		return stringRepresentation.get();
	}
	
	public void setPruner(HUIPruner pruner) {
		this.pruner = pruner;
	}
	
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
		final long finishTime = System.currentTimeMillis() + ms;
		LOG.trace("TreeSearchPlayer selecting a move...");
		
		final BestResult result = new BestResult();
		final List<HUIMove> legalMoves = new ArrayList<>(((HUIGameState) game).getLegalMovesList());
		final ExecutorService threadPool = Executors.newFixedThreadPool(legalMoves.size());
		
		for (HUIMove move : legalMoves) {
			LOG.trace("Evaluate move {}  with depth {}", move, level);
			threadPool.execute(() -> {
				double rating = evaluate(game, move, role, level, ms);
				
				if (!Thread.interrupted() && (rating > result.bestRating)) {
					result.bestMove = move;
					result.bestRating = rating;
				}
			});
		}
		
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(finishTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO: Implement proper thread interruption so the unused threads won't run uselessly in the background
			// Do nothing, just proceed to return.
		}
		
		return result.bestMove;
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
		return pruner.pruneMove(
				(HUIGameState) game,
				(HUIGameState) game.spawnChild(move),
				(HUIMove) move,
				HUIPlayerColor.of(role)
		);
	}

	@Override
	public String toString() {
		return getPlayerName();
	}
}
