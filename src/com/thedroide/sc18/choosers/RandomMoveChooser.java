package com.thedroide.sc18.choosers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * A move chooser that picks it's moves completely randomly. This
 * is almost always a really bad idea, because it will play horribly
 * and get stuck in a loop.
 */
public class RandomMoveChooser implements MoveChooser {
	private static final Random RANDOM = ThreadLocalRandom.current();
	
	@Override
	public Move chooseMove(GameState state) {
		List<Move> moves = state.getPossibleMoves();
		return moves.get(RANDOM.nextInt(moves.size()));
	}
}
