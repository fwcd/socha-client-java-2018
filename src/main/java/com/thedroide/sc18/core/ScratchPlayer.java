package com.thedroide.sc18.core;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.Player;

/**
 * An empty Player implementation to minimize
 * implementation efforts.
 */
public abstract class ScratchPlayer implements Player {
	@Override
	public boolean canPlayGame(GamePlay game) {
		return game instanceof HUIGameState;
	}

	@Override
	public double evaluate(GamePlay game, GameMove move, int[] role, int level, long milliseconds) {
		throw new UnsupportedOperationException("Evaluation not supported.");
	}

	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role) {
		throw new UnsupportedOperationException("Heuristic not supported.");
	}

	@Override
	public boolean pruneMove(GamePlay game, GameMove move, int[] role) {
		return false;
	}

	@Override
	public GameMove selectMove(GamePlay game, int[] role, int level, long milliseconds) throws CannotPlayGameException {
		return pickMove((HUIGameState) game, HUIPlayerColor.of(role), level, milliseconds);
	}
	
	protected abstract HUIMove pickMove(HUIGameState game, HUIPlayerColor player, int depth, long ms);
}
