package com.thedroide.sc18.implementation;

import java.util.concurrent.ForkJoinPool;

import com.thedroide.sc18.algorithmics.Algorithm;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

public class MinimaxAlgorithm implements Algorithm {
	private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	private int depth = 2;
	
	@Override
	public Move getBestMove(GameState state) {
		MinimaxBoardState tree = new MinimaxBoardState(state, depth);
		
		// GUILogger.log("Calculating move...");
		
		FORK_JOIN_POOL.execute(tree);
		tree.join();
		
		// new TreePlotter(tree);
		
		Move move = tree.getBestMove();
		
		// GUILogger.log("Done calculating move...");
		// GUILogger.log(move);
		
		return move;
	}
}
