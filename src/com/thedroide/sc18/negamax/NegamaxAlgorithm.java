package com.thedroide.sc18.negamax;

import java.util.concurrent.ForkJoinPool;

import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;
import com.thedroide.sc18.algorithmics.Algorithm;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.debug.TreePlotter;
import com.thedroide.sc18.evaluators.SmartEvaluator;
import com.thedroide.sc18.utils.IntRating;

public class NegamaxAlgorithm implements Algorithm {
	private final ForkJoinPool forkJoinPool = new ForkJoinPool();
	private final TreePlotter plotter = new TreePlotter(); // TODO: Might remove in the future / Only for debugging
	
	private final int depth = 3;
	private final Evaluator evaluator = new SmartEvaluator();
	
	@Override
	public AMove getBestMove(ABoardState state) {
		NegamaxNode tree = new NegamaxNode(
				depth,
				state,
				evaluator,
				IntRating.getMin(),
				IntRating.getMax(),
				IntRating.getMin()
		);
		
		forkJoinPool.execute(tree);
		tree.quietlyJoin();
		plotter.setTree(tree);
		
		return tree.getBestMove();
	}
}
