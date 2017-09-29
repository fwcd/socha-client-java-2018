package com.thedroide.sc18.negamax;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import com.sun.istack.internal.Nullable;
import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.algorithmics.GraphTreeNode;
import com.thedroide.sc18.algorithmics.Rating;

public class NegamaxNode extends RecursiveAction implements GraphTreeNode, Comparable<NegamaxNode> {
	private static final long serialVersionUID = 3897798L;
	private static final boolean ALPHA_BETA_CUT = true;
	
	private List<NegamaxNode> children = new ArrayList<>();
	private NegamaxNode bestChild;
	
	private final int depth;
	private final Rating negInf;
	
	private Rating alpha;
	private Rating beta;
	private Rating rating;
	private Evaluator evaluator;
	
	@Nullable
	private AMove lastMove;
	private ABoardState state;
	
	/**
	 * Constructs a new negamax-tree.
	 * 
	 * @param depth - The decrementing depth of the tree.
	 * @param state - The current board state
	 * @param evaluator - The move evaluator
	 * @param alpha - The initial alpha value, should be -Infinity when first called
	 * @param beta - The intiial beta value, should be +Infinity when first called
	 * @param negInf - Should be -Infinity
	 */
	public NegamaxNode(
			int depth,
			ABoardState state,
			Evaluator evaluator,
			Rating alpha,
			Rating beta,
			Rating negInf
	) {
		this.depth = depth;
		this.state = state;
		this.evaluator = evaluator;
		
		this.alpha = alpha;
		this.beta = beta;
		this.negInf = negInf;
		
		rating = negInf;
	}

	@Override
	protected void compute() {
		if (isLeaf()) {
			rating = evaluator.evaluate(lastMove, state.getInverted());
		} else {
			for (AMove move : state.getPossibleMoves()) {
				ABoardState nextState = state.copy();
				move.performOn(nextState);
				// TODO: Debug - switchTurns() here or not?
				
				NegamaxNode child = new NegamaxNode(
						depth - 1,
						nextState,
						evaluator,
						beta.invert(),
						alpha.invert(),
						negInf
				);
				child.lastMove = move;
				
				children.add(child);
				child.quietlyInvoke();
				
				Rating childRating = child.rating;
				
				// Maximize rating and child rating
				if (childRating.compareTo(rating) > 0) {
					rating = childRating;
					bestChild = child;
				}
				
				alpha = alpha.max(childRating);
				
				if (ALPHA_BETA_CUT && alpha.compareTo(beta) >= 0) {
					break;
				}
			}
		}
	}
	
	public AMove getBestMove() {
		return bestChild.lastMove;
	}
	
	public NegamaxNode getBestChild() {
		return bestChild;
	}
	
	@Override
	public boolean isLeaf() {
		return depth <= 0;
	}

	@Override
	public List<? extends GraphTreeNode> getChildren() {
		return children;
	}

	@Override
	public String getNodeDescription() {
		return rating.toString();
	}

	@Override
	public int compareTo(NegamaxNode o) {
		return rating.compareTo(o.rating);
	}
}
