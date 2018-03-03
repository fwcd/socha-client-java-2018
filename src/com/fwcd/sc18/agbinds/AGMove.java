package com.fwcd.sc18.agbinds;

import com.antelmann.game.GameMove;

import sc.plugin2018.Move;

public class AGMove implements GameMove {
	private static final long serialVersionUID = -4496433561448177788L;
	private final Move move;
	private final AGPlayerColor player;
	
	public AGMove(Move move, AGPlayerColor player) {
		this.move = move;
		this.player = player;
	}
	
	public Move get() { return move; }
	
	@Override
	public int getPlayer() { return player.asRole(); }
}
