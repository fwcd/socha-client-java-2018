package com.thedroide.sc18.mcts;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.Player;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIPlayerColor;

public class MCTSPlayer implements Player {
	@Override
	public String getPlayerName() {
		return "MCTSPlayer";
	}

	@Override
	public boolean canPlayGame(GamePlay game) {
		return game instanceof HUIGameState;
	}

	@Override
	public GameMove selectMove(GamePlay game, int[] role, int level, long milliseconds) throws CannotPlayGameException {
		if (!canPlayGame(game)) {
			throw new CannotPlayGameException(this, game, "Can't play game.");
		}
		
		long start = System.currentTimeMillis();
		MCTSNode node = new MCTSNode(HUIPlayerColor.of(role), (HUIGameState) game);
		
		while ((System.currentTimeMillis() - start) < milliseconds) {
			node.performIteration();
		}
		
		return node.mostExploredChild().getMove();
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
