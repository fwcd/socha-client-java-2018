package com.thedroide.sc18.implementation;

import java.util.concurrent.ForkJoinPool;

import com.thedroide.sc18.GUILogger;
import com.thedroide.sc18.algorithmics.Algorithm;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

public class MinimaxAlgorithm implements Algorithm {
	private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	private int depth = 1;
	
	@Override
	public Move getBestMove(GameState state) {
		MinimaxBoardState tree = new MinimaxBoardState(state, depth);
		
		FORK_JOIN_POOL.execute(tree);
		tree.join();
		
		GUILogger.log("done");
		
		return tree.getBestMove();
	}
}
