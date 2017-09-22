package com.thedroide.sc18.minimax;

import java.util.concurrent.ForkJoinPool;

import com.thedroide.sc18.algorithmics.Algorithm;
import com.thedroide.sc18.algorithmics.Strategy;
import com.thedroide.sc18.debug.TreePlotter;
import com.thedroide.sc18.strategies.SmartStrategy;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

public class MinimaxAlgorithm implements Algorithm {
	private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	private final TreePlotter plotter = new TreePlotter();
	
	private final Strategy strategy = new SmartStrategy();
	private int depth = 3;
	
	@Override
	public Move getBestMove(GameState state) {
		MinimaxBoardState tree = new MinimaxBoardState(state, strategy, depth);
		
		// GUILogger.log("Calculating move...");
		
		FORK_JOIN_POOL.execute(tree);
		tree.join();
		
		plotter.setTree(tree);
		
		Move move = tree.getMove();
		
		// GUILogger.log("Done calculating move...");
		// GUILogger.log(move);
		
		return move;
	}
}
