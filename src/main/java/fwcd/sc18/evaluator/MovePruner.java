package fwcd.sc18.evaluator;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.shared.PlayerColor;

public interface MovePruner {
	boolean shouldPrune(Move move, PlayerColor myColor, GameState gameBeforeMove, GameState gameAfterMove);
}
