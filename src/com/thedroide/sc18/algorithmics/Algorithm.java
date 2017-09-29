package com.thedroide.sc18.algorithmics;

/**
 * An algorithm that claims to find a "best move"
 * for a given state.
 */
public interface Algorithm {
	public AMove getBestMove(ABoardState state);
}
