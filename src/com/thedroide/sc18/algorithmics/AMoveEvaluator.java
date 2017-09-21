package com.thedroide.sc18.algorithmics;

public interface AMoveEvaluator<T extends ABoard<?>> {
	public AMoveRating<T> evaluate(AMove<T> move);
}
