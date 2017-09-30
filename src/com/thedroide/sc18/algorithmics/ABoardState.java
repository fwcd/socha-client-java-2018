package com.thedroide.sc18.algorithmics;

import java.util.List;

public interface ABoardState {
	public ABoard getBoard();
	
	public List<AMove> getPossibleMoves();
	
	public APlayer getCurrentPlayer();
	
	public void switchTurns();
	
	public ABoardState copy();
	
	public default ABoardState getInverted() {
		ABoardState copy = copy();
		copy.switchTurns();
		return copy;
	}
}
