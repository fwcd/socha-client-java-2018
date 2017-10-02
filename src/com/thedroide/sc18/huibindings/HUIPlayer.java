package com.thedroide.sc18.huibindings;

import com.thedroide.sc18.algorithmics.ABoard;
import com.thedroide.sc18.algorithmics.AField;
import com.thedroide.sc18.algorithmics.APlayer;

import sc.plugin2018.Player;
import sc.shared.PlayerColor;

public class HUIPlayer implements APlayer {
	private final Player player;
	private final ABoard board;
	
	public HUIPlayer(ABoard board, Player player) {
		this.board = board;
		this.player = player;
	}

	@Override
	public int getCarrots() {
		return player.getCarrots();
	}

	@Override
	public int getSalads() {
		return player.getSalads();
	}
	
	@Override
	public String toString() {
		return player.getDisplayName() + ": " + player.getPlayerColor().toString();
	}

	@Override
	public AField getField() {
		return board.getFields().get(player.getFieldIndex());
	}

	@Override
	public PlayerColor getColor() {
		return player.getPlayerColor();
	}
}
