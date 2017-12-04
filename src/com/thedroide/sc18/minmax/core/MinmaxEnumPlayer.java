package com.thedroide.sc18.minmax.core;

import com.antelmann.game.Player;

import sc.shared.PlayerColor;

/**
 * An enumeration holding an instance of
 * every player in the game with it's associated
 * color and HUIPlayer.
 */
public enum MinmaxEnumPlayer {
	RED(0, new MinmaxPlayer()),
	BLUE(1, new MinmaxPlayer());
	
	private final int id;
	private final Player player;
	
	/**
	 * Internal constructor for a new player
	 * enum value.
	 * 
	 * @param id - The identifier or role of this player
	 * @param player - The Player object that does all the AI logic
	 */
	private MinmaxEnumPlayer(int id, Player player) {
		this.id = id;
		this.player = player;
	}
	
	/**
	 * Fetches a {@link MinmaxEnumPlayer} for a given set of
	 * "roles". This is mostly used as a convenience
	 * method when dealing with the Antelmann-Game-API.
	 * 
	 * @param roles - An array that stores the ID of the player in the first slot
	 * @return That player (if found), otherwise it throws an {@link IllegalArgumentException}
	 */
	public static MinmaxEnumPlayer of(int... roles) {
		for (MinmaxEnumPlayer player : values()) {
			if (player.getID() == roles[0]) {
				return player;
			}
		}
		
		throw new IllegalArgumentException("Couldn't find a player matching the given roles.");
	}
	
	/**
	 * A bridge-method that converts a {@link PlayerColor} (Software Challenge API) to
	 * a {@link MinmaxEnumPlayer}.
	 * 
	 * @param scPlayerColor - The {@link PlayerColor} instance
	 * @return The {@link MinmaxEnumPlayer}
	 */
	public static MinmaxEnumPlayer of(PlayerColor scPlayerColor) {
		switch (scPlayerColor) {
		
		case RED:
			return RED;
		case BLUE:
			return BLUE;
		default:
			throw new RuntimeException("Invalid player color!");
		
		}
	}

	public static MinmaxEnumPlayer of(sc.plugin2018.Player player) {
		return of(player.getPlayerColor());
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
	 * A bridge method between {@link MinmaxEnumPlayer} and
	 * the associated {@link sc.plugin2018.Player} (Software Challenge API).
	 * 
	 * @param state - The current game state
	 * @return The Player from the Software Challenge API
	 */
	public sc.plugin2018.Player getSCPlayer(MinmaxGamePlay state) {
		return state.getSCPlayer(this);
	}
	
	/**
	 * Fetches the associated player object that
	 * carries the AI logic. Mainly used by the
	 * Antelmann-Game-API.
	 * 
	 * @return The associated {@link Player}
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Fetches the opponent of this {@link MinmaxEnumPlayer}.
	 * 
	 * @return The opponent
	 */
	public MinmaxEnumPlayer getOpponent() {
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
