package com.thedroide.sc18.mcts.choosers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;

/**
 * A move chooser that picks it's moves completely randomly. This
 * is almost always a really bad idea, because it will play horribly
 * and get stuck in a loop.
 */
public class RandomMoveChooser implements MCTSMoveChooser {
	private static final Random RANDOM = ThreadLocalRandom.current();
	
	@Override
	public Move chooseMove(GameState state, Player currentPlayer) {
		List<Move> moves = state.getPossibleMoves();
		return moves.get(RANDOM.nextInt(moves.size()));
	}
}
