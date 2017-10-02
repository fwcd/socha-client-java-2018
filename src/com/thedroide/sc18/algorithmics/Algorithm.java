package com.thedroide.sc18.algorithmics;

import com.thedroide.sc18.utils.SimpleMove;

import sc.plugin2018.GameState;

public interface Algorithm {
	public SimpleMove getBestMove(GameState state);
}
