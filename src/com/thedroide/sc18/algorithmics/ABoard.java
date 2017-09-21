package com.thedroide.sc18.algorithmics;

public interface ABoard<T extends ABoard<?>> {
	public T perform(AMove<T> move);
}
