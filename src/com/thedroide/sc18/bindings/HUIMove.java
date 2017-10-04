package com.thedroide.sc18.bindings;

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
public class HUIMove implements GameMove {
	private static final long serialVersionUID = -8856272531609224268L;

	private Move move;
	private HUIEnumPlayer player;
	
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
//		return move.getActions().hashCode();
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
//		return move.equals(obj);
//		return move.getActions().equals(((HUIMove) obj).move.getActions());
		return toString().equals(obj.toString());
	}
	
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
}
