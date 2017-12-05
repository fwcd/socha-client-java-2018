package com.thedroide.sc18.choosers;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;

/**
 * An interface that provides a (simple)
 * move chooser.
 */
@FunctionalInterface
public interface MoveChooser {
	HUIMove chooseMove(HUIGameState state);
}
