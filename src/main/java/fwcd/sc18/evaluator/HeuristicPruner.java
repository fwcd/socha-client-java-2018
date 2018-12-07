package fwcd.sc18.evaluator;

import fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.Action;
import sc.plugin2018.ExchangeCarrots;
import sc.plugin2018.FallBack;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.PlayerColor;

public class HeuristicPruner implements MovePruner {
	public boolean shouldPrune(Move move, PlayerColor myColor, GameState gameBeforeMove, GameState gameAfterMove) {
		Player meBeforeMove = gameBeforeMove.getPlayer(myColor);
		Player meAfterMove = gameAfterMove.getPlayer(myColor);

		Action lastAction = meBeforeMove.getLastNonSkipAction();
		Class<? extends Action> lastActionClass = (lastAction == null) ? null : lastAction.getClass();
		Class<? extends Action> actionClass = meAfterMove.getLastNonSkipAction().getClass();
		boolean shouldNotFallBack = meAfterMove.getCarrots() > 10 || meAfterMove.getFieldIndex() < HUIUtils.LAST_SALAD_FIELD;

		return (lastActionClass == ExchangeCarrots.class && actionClass == ExchangeCarrots.class)
				|| (actionClass == FallBack.class && shouldNotFallBack);
	}
}
