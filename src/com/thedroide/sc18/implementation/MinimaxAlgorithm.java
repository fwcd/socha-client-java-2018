package com.thedroide.sc18.implementation;

import java.util.concurrent.ForkJoinPool;

import com.thedroide.sc18.algorithmics.Algorithm;
import com.thedroide.sc18.debug.GUILogger;
import com.thedroide.sc18.debug.GraphPlotter;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

public class MinimaxAlgorithm implements Algorithm {
	private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	private int depth = 3;
	
	@Override
	public Move getBestMove(GameState state) {
		MinimaxBoardState tree = new MinimaxBoardState(state, depth);
		
		GUILogger.log("let's go");
		
		FORK_JOIN_POOL.execute(tree);
		tree.join();
		
		new GraphPlotter(tree);
		
		GUILogger.log("done");
		
		return tree.getBestMove();
	}
}
