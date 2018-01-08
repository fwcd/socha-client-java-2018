package com.thedroide.sc18.choosers;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.Player;
import com.thedroide.sc18.core.HUIGameState;

/**
 * Allows a {@link MoveChooser} to be used as a {@link Player}.
 * This is mostly for debug reasons.
 */
public class BasicPlayer implements Player {
	private final MoveChooser chooser;

	public BasicPlayer(MoveChooser chooser) {
		this.chooser = chooser;
	}
	
	@Override
	public String getPlayerName() {
		return chooser.getClass().getSimpleName();
	}

	@Override
	public GameMove selectMove(GamePlay game, int[] role, int level, long milliseconds) throws CannotPlayGameException {
		return chooser.chooseMove((HUIGameState) game);
	}

	@Override
	public boolean canPlayGame(GamePlay game) {
		return game instanceof HUIGameState;
	}

	@Override
	public double evaluate(GamePlay game, GameMove move, int[] role, int level, long milliseconds)
			throws CannotPlayGameException, GameRuntimeException {
		return 0;
	}

	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role)
			throws CannotPlayGameException, GameRuntimeException {
		return 0;
	}

	@Override
	public boolean pruneMove(GamePlay game, GameMove move, int[] role) {
		return false;
	}
}
