package com.thedroide.sc18.choosers;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * An interface that provides a (simple)
 * move chooser.
 */
@FunctionalInterface
public interface MoveChooser {
	Move chooseMove(GameState state);
}
