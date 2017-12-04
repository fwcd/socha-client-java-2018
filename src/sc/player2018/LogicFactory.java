package sc.player2018;

import com.thedroide.sc18.mcts.MCTSLogic;
import com.thedroide.sc18.minmax.MinmaxLogic;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.IGameHandler;

/**
 * Stores the available game logics and the associated
 * constructors.
 */
public enum LogicFactory {
	SIMPLE(SimpleLogic::new), // The SimpleClient-logic
	MINMAX(MinmaxLogic::new), // A smart minimax-alpha-beta approach
	MCTS(MCTSLogic::new); // A monte-carlo-tree-search
	
	private final LogicBuilder builder;
	
	private LogicFactory(LogicBuilder builder) {
		this.builder = builder;
	}
	
	/**
	 * Fetches the logic used. (This may be changed)
	 */
	public static LogicFactory getDefault() {
		return MCTS; // FIXME: Temporary
	}

	/**
	 * Creates and returns a new logic instance.
	 */
	public IGameHandler createInstance(AbstractClient client) throws Exception {
		return builder.build(client);
	}
}
