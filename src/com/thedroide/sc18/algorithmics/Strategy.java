package com.thedroide.sc18.algorithmics;

import java.util.Optional;

/**
 * A strategy acts as a filter, returning a potentially
 * good move for "obvious" situations (like reaching
 * a goal).<br><br>
 * 
 * It might be a good idea to run an instance of this
 * as a first-priority-check (before building any game
 * trees etc).
 */
public interface Strategy {
	/**
	 * May or may not return a "best move" for
	 * the given state.
	 * 
	 * @param state - The given game state
	 * @return Optionally the "best move" for this state
	 */
	public Optional<AMove> perform(ABoardState state);
}
