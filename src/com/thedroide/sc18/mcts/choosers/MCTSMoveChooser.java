package com.thedroide.sc18.mcts.choosers;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;

/**
 * A move picker used for
 * monte-carlo-tree-search simulation.
 */
@FunctionalInterface
public interface MCTSMoveChooser {
	Move chooseMove(GameState state, Player currentPlayer);
}
