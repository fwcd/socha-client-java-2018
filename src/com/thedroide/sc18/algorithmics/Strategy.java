package com.thedroide.sc18.algorithmics;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * A "Strategy" that will evaluate a certain move.
 * 
 * @param <T> - Rating type
 */
public interface Strategy {
	public MoveRating evaluate(Move move, GameState state);
}
