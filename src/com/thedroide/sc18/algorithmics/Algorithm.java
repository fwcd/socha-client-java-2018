package com.thedroide.sc18.algorithmics;

public interface Algorithm<T extends ABoard<?>> {
	public AMove<T> getBestMove(T board);
}
