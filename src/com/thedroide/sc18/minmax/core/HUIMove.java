package com.thedroide.sc18.minmax.core;

import java.util.Iterator;

import com.antelmann.game.GameMove;

import sc.plugin2018.Action;
import sc.plugin2018.Advance;
import sc.plugin2018.Card;
import sc.plugin2018.EatSalad;
import sc.plugin2018.ExchangeCarrots;
import sc.plugin2018.FallBack;
import sc.plugin2018.Move;
import sc.plugin2018.Skip;

/**
 * Represents a move in the game.<br><br>
 * 
 * Used as a bridge-implementation between the
 * Software Challenge API and the Antelmann-Game-API.
 */
public class HUIMove implements GameMove, Iterable<Action> {
	private static final long serialVersionUID = -8856272531609224268L;

	private final Move move;
	private final HUIEnumPlayer player;
	
	private boolean discarded = false;
	
	public HUIMove(HUIEnumPlayer player, Move move) {
		if (move == null) {
			throw new NullPointerException("Move shouldn't be null!");
		}
		
		this.move = move;
		this.player = player;
	}
	
	/**
	 * Fetches the underlying {@link Move}
	 * (Software Challenge API).
	 * 
	 * @return The underlying Move object
	 */
	public Move getSCMove() {
		return move;
	}

	@Override
	public int getPlayer() {
		return player.getID();
	}
	
	@Override
	public int hashCode() {
		return move.getActions().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return move.equals(obj);
	}
	
	/**
	 * Fetches the total fields advanced by the player
	 * with this move.<br><br>
	 * 
	 * <b>Note that this method runs in
	 * linear time and might require "computationally expensive"
	 * instanceof-checks.</b>
	 * 
	 * @return The amount of fields moved forward
	 */
	public int getFieldsDelta() {
		int fieldsDelta = 0;
		
		for (Action action : move.getActions()) {
			if (action instanceof Advance) {
				fieldsDelta += ((Advance) action).getDistance();
			}
		}
		
		return fieldsDelta;
	}
	
	public boolean isSkip() {
		for (Action action : move.getActions()) {
			if (action instanceof Skip) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Marks this move as "useless". <b>Use this with caution!</b>
	 */
	public void discard() {
		discarded = true;
	}
	
	public boolean isDiscarded() {
		return discarded;
	}
	
	/**
	 * Provides a concise string representation of a game move.
	 */
	@Override
	public String toString() {
		String s = "[Move: ";
		
		for (Action action : move.getActions()) {
			if (action instanceof Advance) {
				s += "(Advance -> " + Integer.toString(((Advance) action).getDistance()) + ") ";
			} else if (action instanceof EatSalad) {
				s += "(EatSalad) ";
			} else if (action instanceof Card) {
				s += "(Card) ";
			} else if (action instanceof ExchangeCarrots) {
				s += "(ExchangeCarrots) ";
			} else if (action instanceof FallBack) {
				s += "(FallBack) ";
			} else if (action instanceof Skip) {
				s += "(Skip) ";
			}
		}
		
		return s + "]";
	}

	@Override
	public Iterator<Action> iterator() {
		return move.getActions().iterator();
	}
}
