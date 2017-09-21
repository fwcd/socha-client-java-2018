package com.thedroide.sc18.algorithmics;

import sc.plugin2018.Board;
import sc.plugin2018.Move;

public interface Algorithm {
	public Move getBestMove(Board board);
}
