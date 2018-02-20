package com.fwcd.sc18.trainer.core;

import sc.plugin2018.Move;
import sc.shared.PlayerColor;

public class VirtualClient {
	private final PlayerColor color;
	private Move move;
	
	public VirtualClient(PlayerColor color) {
		this.color = color;
	}
	
	public PlayerColor getColor() {
		return color;
	}
	
	public void sendMove(Move move) {
		this.move = move;
	}
	
	public Move getLastMove() {
		return move;
	}
}
