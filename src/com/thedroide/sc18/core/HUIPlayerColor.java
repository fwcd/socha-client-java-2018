package com.thedroide.sc18.core;

import sc.shared.PlayerColor;

/**
 * An enumeration holding an instance of
 * every player in the game with it's associated
 * color and HUIPlayer.
 */
public enum HUIPlayerColor {
	RED(0),
	BLUE(1);
	
	private final int id;
	
	/**
	 * Internal constructor for a new player
	 * enum value.
	 * 
	 * @param id - The identifier or role of this player
	 * @param player - The Player object that does all the AI logic
	 */
	private HUIPlayerColor(int id) {
		this.id = id;
	}
	
	/**
	 * Fetches a {@link HUIPlayerColor} for a given set of
	 * "roles". This is mostly used as a convenience
	 * method when dealing with the Antelmann-Game-API.
	 * 
	 * @param roles - An array that stores the ID of the player in the first slot
	 * @return That player (if found), otherwise it throws an {@link IllegalArgumentException}
	 */
	public static HUIPlayerColor of(int... roles) {
		for (HUIPlayerColor player : values()) {
			if (player.getID() == roles[0]) {
				return player;
			}
		}
		
		throw new IllegalArgumentException("Couldn't find a player matching the given roles.");
	}
	
	/**
	 * A bridge-method that converts a {@link PlayerColor} (Software Challenge API) to
	 * a {@link HUIPlayerColor}.
	 * 
	 * @param scPlayerColor - The {@link PlayerColor} instance
	 * @return The {@link HUIPlayerColor}
	 */
	public static HUIPlayerColor of(PlayerColor scPlayerColor) {
		switch (scPlayerColor) {
		
		case RED:
			return RED;
		case BLUE:
			return BLUE;
		default:
			throw new RuntimeException("Invalid player color!");
		
		}
	}

	public static HUIPlayerColor of(sc.plugin2018.Player player) {
		return of(player.getPlayerColor());
	}
	
	/**
	 * A bridge method between {@link HUIPlayerColor} and
	 * the associated {@link sc.plugin2018.Player} (Software Challenge API).
	 * 
	 * @param state - The current game state
	 * @return The Player from the Software Challenge API
	 */
	public sc.plugin2018.Player getSCPlayer(HUIGameState state) {
		return state.getSCPlayer(this);
	}
	
	/**
	 * Fetches the opponent of this {@link HUIPlayerColor}.
	 * 
	 * @return The opponent
	 */
	public HUIPlayerColor getOpponent() {
		switch (this) {
		
		case RED:
			return BLUE;
		case BLUE:
			return RED;
		default:
			throw new RuntimeException("Invalid player color!");
		
		}
	}
	
	/**
	 * Fetches an associated ID or "role" of this
	 * player. Mainly used by the Antelmann-Game-API.
	 * 
	 * @return The associated ID
	 */
	public int getID() {
		return id;
	}
}
