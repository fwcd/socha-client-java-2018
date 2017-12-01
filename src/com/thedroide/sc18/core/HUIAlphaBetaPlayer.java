package com.thedroide.sc18.core;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.TemplatePlayer;
import com.thedroide.sc18.strategies.HUIHeuristic;
import com.thedroide.sc18.strategies.SmartHeuristic;

/**
 * Represents an abstract ai player delegating to
 * a {@link HUIHeuristic} that can evaluate a given game state.
 */
public class HUIAlphaBetaPlayer extends TemplatePlayer {
	private static final long serialVersionUID = -2746100695353269130L;
	private final HUIHeuristic heuristic = new SmartHeuristic();
	
	public HUIAlphaBetaPlayer() {
		super("HUIPlayer", 2, true);
	}
	
	/**
	 * Checks if the given GamePlay is a valid
	 * "Hase und Igel"-GamePlay.
	 */
	@Override
	public boolean canPlayGame(GamePlay game) {
		if (game instanceof HUIGamePlay) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Calculates a domain-specific heuristic for
	 * "Hase und Igel". Delegates to {@link HUIHeuristic}.
	 */
	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role) throws CannotPlayGameException, GameRuntimeException {
		try {
			return heuristic.heuristic((HUIGamePlay) game, (HUIMove) move, HUIEnumPlayer.of(role));
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
			return heuristic.pruneMove((HUIGamePlay) game, (HUIMove) move, HUIEnumPlayer.of(role));
		} catch (ClassCastException e) {
			throw new CannotPlayGameException(this, game, "Invalid game type.");
		}
	}
}
