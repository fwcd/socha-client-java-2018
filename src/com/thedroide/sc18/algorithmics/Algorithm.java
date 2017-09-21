package com.thedroide.sc18.algorithmics;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

public interface Algorithm {
	public Move getBestMove(GameState state);
}
