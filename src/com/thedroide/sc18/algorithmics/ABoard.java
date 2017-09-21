package com.thedroide.sc18.algorithmics;

import java.util.List;

public interface ABoard<T extends ABoard<?>> {
	public T perform(AMove<T> move);
	
	public List<AMove<T>> getPossibleMoves();
}
