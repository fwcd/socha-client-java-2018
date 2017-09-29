package com.thedroide.sc18.huibindings;

import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;

import sc.plugin2018.Move;
import sc.shared.InvalidMoveException;

/**
 * A Move wrapper simplifying API calls
 */
public class HUIMove implements AMove {
	private final Move move;
	
	/**
	 * Constructs a new HUIMove.
	 * 
	 * @param move - The move
	 */
	public HUIMove(Move move) {
		this.move = move;
	}

	@Override
	public void performOn(ABoardState boardState) {
		try {
			HUIBoardState huiState = (HUIBoardState) boardState;
			move.perform(huiState.getSCState());
		} catch (InvalidMoveException e) {
			e.printStackTrace();
		}
	}

	public Move getSCMove() {
		return move;
	}
}
