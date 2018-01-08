package com.thedroide.sc18.choosers;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;

/**
 * A move chooser that picks it's moves completely randomly. This
 * is almost always a really bad idea, because it will play horribly
 * and get stuck in a loop.
 */
public class RandomMoveChooser implements MoveChooser {
	private static final Random RANDOM = ThreadLocalRandom.current();
	
	@Override
	public HUIMove chooseMove(HUIGameState state) {
		HUIMove[] moves = state.getLegalMoves();
		return moves[RANDOM.nextInt(moves.length)];
	}
}
