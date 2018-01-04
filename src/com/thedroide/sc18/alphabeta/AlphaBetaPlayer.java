package com.thedroide.sc18.alphabeta;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.TemplatePlayer;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;
import com.thedroide.sc18.heuristics.HUIHeuristic;
import com.thedroide.sc18.heuristics.SmartHeuristic;

/**
 * Represents a minimax-alpha-beta player delegating to
 * a {@link HUIHeuristic} to evaluate a given game state.
 */
public class AlphaBetaPlayer extends TemplatePlayer {
	private static final long serialVersionUID = -2746100695353269130L;
	private final HUIHeuristic heuristic = new SmartHeuristic();
	
	public AlphaBetaPlayer() {
		super("AlphaBetaPlayer", 2, true);
	}
	
	/**
	 * Checks if the given GamePlay is a valid
	 * "Hase und Igel"-GamePlay.
	 */
	@Override
	public boolean canPlayGame(GamePlay game) {
		return game instanceof HUIGameState;
	}

	private HUIGameState getChild(HUIGameState state, HUIMove move) {
		return state.spawnChild(move);
	}
	
	/**
	 * Calculates a domain-specific heuristic for
	 * "Hase und Igel". Delegates to {@link HUIHeuristic}.
	 */
	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role)
			throws CannotPlayGameException, GameRuntimeException {
		try {
			return heuristic.heuristic(
					(HUIGameState) game,
					getChild((HUIGameState) game, (HUIMove) move),
					(HUIMove) move,
					HUIPlayerColor.of(role)
			);
		} catch (ClassCastException e) {
			throw new CannotPlayGameException(this, game, "Invalid game type.");
		}
	}

	/**
	 * Decides in domain-specific way if a move should
	 * be prunes. Delegates to {@link HUIHeuristic}.
	 */
	@Override
	public boolean pruneMove(GamePlay game, GameMove move, int[] role) {
		try {
			return heuristic.pruneMove(
					(HUIGameState) game,
					getChild((HUIGameState) game, (HUIMove) move),
					(HUIMove) move,
					HUIPlayerColor.of(role)
			);
		} catch (ClassCastException e) {
			throw new CannotPlayGameException(this, game, "Invalid game type.");
		}
	}
}
