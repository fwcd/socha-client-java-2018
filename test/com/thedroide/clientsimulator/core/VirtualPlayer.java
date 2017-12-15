package com.thedroide.clientsimulator.core;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import com.antelmann.game.Player;
import com.thedroide.sc18.core.HUIPlayerColor;

import sc.shared.PlayerColor;

public class VirtualPlayer {
	private final String name;
	private final Player ai;
	private PlayerColor color = null;
	private AtomicInteger score = new AtomicInteger(0);
	
	public VirtualPlayer(Player ai) {
		this.ai = ai;
		name = ai.getPlayerName();
	}
	
	public void setColor(PlayerColor color) {
		this.color = color;
	}
	
	public HUIPlayerColor getHUIPlayerColor() {
		return HUIPlayerColor.of(getSCPlayerColor());
	}
	
	public PlayerColor getSCPlayerColor() {
		if (color == null) {
			throw new NoSuchElementException("No color available for this player!");
		}
		
		return color;
	}
	
	public void incrementScore() {
		score.incrementAndGet();
	}
	
	public int getScore() {
		return score.get();
	}
	
	public Player getAI() {
		return ai;
	}
	
	public String getName() {
		return name;
	}
}
