package sc.player2018;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.IGameHandler;

/**
 * Creates a game handler/game logic instance
 * using a given client. This functional interface
 * is intended to minimize reflection usage (mostly
 * for performance reasons).<br><br>
 * 
 * Using Java 8 method reference syntax, this interface
 * can be invoked in a very compact way:<br><br>
 * 
 * {@code SmartLogic::new}
 */
@FunctionalInterface
public interface LogicBuilder {
	IGameHandler build(AbstractClient client);
}
