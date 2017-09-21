package com.thedroide.sc18.algorithmics;

public interface AMove<T extends ABoard<?>> {
	public T perform(T board);
}
