package com.thedroide.sc18.algorithmics;

import sc.shared.PlayerColor;

public interface APlayer {
	public AField getField();
	
	public int getCarrots();
	
	public int getSalads();

	public PlayerColor getColor();
}
