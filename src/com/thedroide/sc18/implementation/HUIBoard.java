package com.thedroide.sc18.implementation;

import com.thedroide.sc18.algorithmics.ABoard;
import com.thedroide.sc18.algorithmics.AMove;

import sc.plugin2018.Board;

public class HUIBoard implements ABoard<HUIBoard> {
	private Board scBoard;
	
	public HUIBoard(Board scBoard) {
		this.scBoard = scBoard;
	}
	
	@Override
	public HUIBoard perform(AMove<HUIBoard> move) {
		return move.perform(this);
	}
	
	public Board getSCBoard() {
		return scBoard;
	}
}
