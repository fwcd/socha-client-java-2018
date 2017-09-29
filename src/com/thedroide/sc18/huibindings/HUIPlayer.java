package com.thedroide.sc18.huibindings;

import java.util.HashMap;
import java.util.Map;

import com.thedroide.sc18.algorithmics.ABoard;
import com.thedroide.sc18.algorithmics.AField;
import com.thedroide.sc18.algorithmics.APlayer;
import com.thedroide.sc18.debug.GUILogger;

import sc.plugin2018.Player;

public class HUIPlayer implements APlayer {
	private static final Map<Player, HUIPlayer> CACHE = new HashMap<>();
	
	private final ABoard board;
	private final Player player;
	
	private HUIPlayer(ABoard board, Player player) {
		this.board = board;
		this.player = player;
	}
	
	public static HUIPlayer of(ABoard board, Player player) {
		if (!CACHE.containsKey(player)) {
			CACHE.put(player, new HUIPlayer(board, player));
		}
		
		return CACHE.get(player);
	}
	
	@Override
	public AField getField() {
		GUILogger.log(player.getPlayerColor() + " @ " + player.getFieldIndex() + " (" + player.getDisplayName() + ")");
		return board.getFields().get(player.getFieldIndex());
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
}
