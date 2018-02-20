package com.fwcd.sc18.core;

import java.util.stream.Stream;

import com.fwcd.sc18.trainer.core.VirtualClient;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;

public abstract class EvaluatingLogic extends TemplateLogic {
	private boolean parallelize = true;
	
	public EvaluatingLogic(VirtualClient client) {
		super(client);
	}
	
	public EvaluatingLogic(AbstractClient client) {
		super(client);
	}
	
	@Override
	protected Move selectMove(GameState gameBeforeMove, Player me) {
		Stream<Move> moves = gameBeforeMove.getPossibleMoves().stream();
		
		if (parallelize) {
			moves = moves.parallel();
		}
		
		return moves
				.max((a, b) -> Float.compare(
						evaluateMove(a, gameBeforeMove, me),
						evaluateMove(b, gameBeforeMove, me)
				))
				.orElseThrow(IllegalStateException::new);
	}
	
	protected abstract float evaluateMove(Move move, GameState gameBeforeMove, Player me);

	public void setParallelize(boolean parallelize) {
		this.parallelize = parallelize;
	}
}