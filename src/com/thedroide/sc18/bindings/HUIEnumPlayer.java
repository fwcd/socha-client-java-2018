package com.thedroide.sc18.bindings;

import com.antelmann.game.Player;

import sc.plugin2018.GameState;
import sc.shared.PlayerColor;

/**
 * An enumeration holding an instance of
 * every player in the game with it's associated
 * color and HUIPlayer.
 */
public enum HUIEnumPlayer {
	RED(0, new HUIPlayer()),
	BLUE(1, new HUIPlayer());
	
	private final int id;
	private final Player player;
	
	private HUIEnumPlayer(int id, Player player) {
		this.id = id;
		this.player = player;
	}
	
	/**
	 * Fetches a {@link HUIEnumPlayer} for a given set of
	 * "roles". This is mostly used as a convenience
	 * method when dealing with the Antelmann-Game-API.
	 * 
	 * @param roles - An array that stores the ID of the player in the first slot
	 * @return That player (if found), otherwise it throws an {@link IllegalArgumentException}
	 */
	public static HUIEnumPlayer of(int... roles) {
		for (HUIEnumPlayer player : values()) {
			if (player.getID() == roles[0]) {
				return player;
			}
		}
		
		throw new IllegalArgumentException("Couldn't find a player matching the given roles.");
	}
	
	/**
	 * A bridge-method that converts a {@link PlayerColor} (Software Challenge API) to
	 * a {@link HUIEnumPlayer}.
	 * 
	 * @param scPlayerColor - The {@link PlayerColor} instance
	 * @return The {@link HUIEnumPlayer}
	 */
	public static HUIEnumPlayer of(PlayerColor scPlayerColor) {
		switch (scPlayerColor) {
		
		case RED:
			return RED;
		case BLUE:
			return BLUE;
		default:
			throw new RuntimeException("Invalid player color!");
		
		}
	}

	/**
	 * Fetches all the {@link Player} instances of this
	 * game.
	 * 
	 * @return An array of {@link Player} instances
	 */
	public static Player[] getPlayers() {
		return new Player[] {BLUE.getPlayer(), RED.getPlayer()};
	}
	
	/**
	 * A bridge method between {@link HUIEnumPlayer} and
	 * the associated {@link sc.plugin2018.Player} (Software Challenge API).
	 * 
	 * @param state - The current game state
	 * @return The Player from the Software Challenge API
	 */
	public sc.plugin2018.Player getSCPlayer(GameState state) {
		switch (this) {
		
		case RED:
			return state.getRedPlayer();
		case BLUE:
			return state.getBluePlayer();
		default:
			throw new RuntimeException("Invalid player color!");
		
		}
	}
	
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Fetches the opponent of this {@link HUIEnumPlayer}.
	 * 
	 * @return The opponent
	 */
	public HUIEnumPlayer getOpponent() {
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
