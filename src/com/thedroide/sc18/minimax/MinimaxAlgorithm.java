package com.thedroide.sc18.minimax;

import java.util.concurrent.ForkJoinPool;

import com.thedroide.sc18.algorithmics.Algorithm;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.debug.GUILogger;
import com.thedroide.sc18.debug.TreePlotter;
import com.thedroide.sc18.minimax.evaluators.SmartEvaluator;
import com.thedroide.sc18.utils.SimpleMove;

import sc.plugin2018.GameState;

public class MinimaxAlgorithm implements Algorithm {
	private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();
	
	private final TreePlotter plotter = new TreePlotter();
	
	private final Evaluator<MinimaxBoardState> strategy = new SmartEvaluator();
	private int depth = 3;
	
	@Override
	public SimpleMove getBestMove(GameState state) {
		MinimaxBoardState tree = new MinimaxBoardState(state, strategy, depth);
		
		// GUILogger.log("Calculating move...");
		
		FORK_JOIN_POOL.execute(tree);
		tree.join();
		
		plotter.setTree(tree);
		
		SimpleMove move = tree.getBestMove();
		
		GUILogger.log("Currently targetting path " + tree.getBestPath());
		
		// GUILogger.log("Done calculating move...");
		// GUILogger.log(move);
		
		return move;
	}
}
