package com.thedroide.sc18.bindings;

import com.antelmann.game.GameMove;

import sc.plugin2018.Move;

public class HUIMove implements GameMove {
	private static final long serialVersionUID = -8856272531609224268L;

	private Move move;
	private HUIEnumPlayer player;
	
	public HUIMove(HUIEnumPlayer player, Move move) {
		this.move = move;
		this.player = player;
	}
	
	public Move getSCMove() {
		return move;
	}

	@Override
	public int getPlayer() {
		return player.getID();
	}
}
