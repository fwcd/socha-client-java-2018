package com.thedroide.sc18.core;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.antelmann.game.AutoPlay;
import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameDriver;
import com.antelmann.game.GameMove;
import com.antelmann.game.Player;

/**
 * A domain-specific {@link AutoPlay} implementation that
 * allows the immutable {@link HUIGameState} to be swapped
 * as opposed to {@link GameDriver}.
 */
public class HUIDriver implements AutoPlay {
	private HUIGameState game;
	private int level;
	private Player[] players;
	private long responseTime = Long.MAX_VALUE;
	
	public HUIDriver(HUIGameState state, int level, Player... players) {
		this.game = state;
		this.level = level;
		this.players = players;
	}
	
	public void setGame(HUIGameState game) {
		this.game = game;
	}
	
	@Override
	public HUIGameState getGame() {
		return game;
	}

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int getLevel() {
		return level;
	}
	
	/**
	 * Replaces the current game state instance with the
	 * child state that follows the "best move". <b>Due to
	 * the immutable nature of {@link HUIGameState} the state
	 * needs to be fetched after autoMove() again! In many cases
	 * it makes more sense to use {@code hint(...)} instead.</b>
	 */
	@Override
	public synchronized GameMove autoMove() {
		HUIMove next = hint(game.nextPlayer());
		game = game.spawnChild(next);
		return next;
	}

	@Override
	public Player getPlayer(int gameRole) {
		return players[gameRole];
	}

	@Override
	public Player[] getPlayers() {
		return players;
	}

	@Override
	public Player changePlayer(int gameRole, Player player) throws CannotPlayGameException {
		if (!player.canPlayGame(game)) {
			throw new CannotPlayGameException(player, game, "Provided player cannot play \"Hase und Igel\"!");
		}
		
		Player old = players[gameRole];
		players[gameRole] = player;
		return old;
	}

	@Override
	public int[] getRoles(Player player) {
		for (int i=0; i<players.length; i++) {
			if (players[i].equals(player)) {
				return new int[] {i};
			}
		}
		
		return null;
	}

	/**
	 * Sets the soft maximum time in milliseconds.
	 */
	@Override
	public void setResponseTime(long ms) {
		responseTime = ms;
	}
	
	/**
	 * Fetches the soft maximum time in milliseconds.
	 */
	@Override
	public long getResponseTime() {
		return responseTime;
	}

	@Override
	public HUIMove hint(int playerRole) {
		if (game.getLegalMovesList().size() == 0) {
			return null;
		} else {
			return (HUIMove) players[playerRole].selectMove(game, new int[] {playerRole}, level, responseTime);
		}
	}

	@Override
	public double evaluateMove(GameMove move) {
		int role = move.getPlayer();
		return players[role].evaluate(game, move, new int[] {role}, level, responseTime);
	}

	@Override
	public HUIMove getRandomLegalMove() {
		List<HUIMove> legalMoves = game.getLegalMovesList();
		return legalMoves.get(ThreadLocalRandom.current().nextInt(legalMoves.size()));
	}
}
