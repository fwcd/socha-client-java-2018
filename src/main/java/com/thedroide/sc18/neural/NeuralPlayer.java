package com.thedroide.sc18.neural;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.Player;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.utils.WIP;

@WIP(usable = false)
public class NeuralPlayer implements Player {
	@Override
	public String getPlayerName() {
		return "NeuralPlayer";
	}

	@Override
	public boolean canPlayGame(GamePlay game) {
		return game instanceof HUIGameState;
	}

	@Override
	public double evaluate(GamePlay game, GameMove move, int[] role, int level, long milliseconds) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GameMove selectMove(GamePlay game, int[] role, int level, long milliseconds) throws CannotPlayGameException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean pruneMove(GamePlay game, GameMove move, int[] role) {
		// TODO Auto-generated method stub
		return false;
	}
}
