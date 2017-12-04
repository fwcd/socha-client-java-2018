package com.thedroide.sc18.minmax.strategies;

import com.thedroide.sc18.choosers.MoveChooser;
import com.thedroide.sc18.choosers.SimpleMoveChooser;
import com.thedroide.sc18.minmax.core.MinmaxGamePlay;
import com.thedroide.sc18.minmax.core.MinmaxMove;

public class SimpleStrategy implements ShallowStrategy {
	private static final MoveChooser CHOOSER = new SimpleMoveChooser();
	
	@Override
	public MinmaxMove bestMove(MinmaxGamePlay gamePlay) {
		return new MinmaxMove(gamePlay.nextHUIEnumPlayer(), CHOOSER.chooseMove(gamePlay.getSCState()));
	}
}
