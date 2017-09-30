package com.thedroide.sc18.negamax;

import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;
import com.thedroide.sc18.algorithmics.Algorithm;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.algorithmics.Strategy;
import com.thedroide.sc18.debug.TreePlotter;
import com.thedroide.sc18.evaluators.SmartEvaluator;
import com.thedroide.sc18.strategies.CaptainObviousStrategy;
import com.thedroide.sc18.utils.IntRating;

public class NegamaxAlgorithm implements Algorithm {
	private final ForkJoinPool forkJoinPool = new ForkJoinPool();
	private final TreePlotter plotter = new TreePlotter();
	
	private final int depth = 2; // Always use an even number!!
	private final Evaluator evaluator = new SmartEvaluator();
	private final Strategy strategy = new CaptainObviousStrategy();
	
	// TODO: Move loop detector ?
	
	@Override
	public AMove getBestMove(ABoardState state) {
		Optional<AMove> strategicMove = strategy.perform(state);
		
		if (strategicMove.isPresent()) {
			return strategicMove.get();
		}
		
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
		
		new Thread(() -> plotter.setTree(tree)).start();
		
		return tree.getBestMove();
	}
}
