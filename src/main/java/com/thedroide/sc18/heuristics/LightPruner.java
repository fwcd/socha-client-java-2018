package com.thedroide.sc18.heuristics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;

import sc.plugin2018.ExchangeCarrots;

/**
 * A hopefully fast game state pruner.
 */
public class LightPruner implements HUIPruner {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	
	@Override
	public boolean pruneMove(
			HUIGameState gameBeforeMove,
			HUIGameState gameAfterMove,
			HUIMove move,
			HUIPlayerColor player
	) {
		try {
			if (gameAfterMove.getWinner() != null
					// TODO: Is excluding double-carrot exchanges actually a bad thing or does it impact performance unnecessarily?
					// An idea might be to limit this to the end game (specifically targetting carrot-loops)
					|| (move.isCarrotExchange() && gameBeforeMove.getSCPlayer(player).getLastNonSkipAction() instanceof ExchangeCarrots)) {
				return true;
			}
			
			return false;
		} catch (Exception e) {
			LOG.warn("Exception while pruning move: ", e);
			return false;
		}
	}
}
