package sc.player2018;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedroide.sc18.SmartLogic;

import sc.player2018.logic.SimpleLogic;
import sc.plugin2018.AbstractClient;
import sc.plugin2018.IGameHandler;

/**
 * Erlaubt es verschiedene Logiken zu verwenden und eine davon auszuwaehlen und
 * Instanzen dieser Logik zu erzeugen
 * 
 * @author and
 */
public enum LogicFactory {
	SIMPLE(SimpleLogic::new), // Die SimpleClient-Logik
	SMART(SmartLogic::new); // Unsere Strategie

	private static final Logger	LOGGER = LoggerFactory.getLogger(LogicFactory.class);
	private final LogicBuilder builder;

	private LogicFactory(LogicBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Erstellt eine Logik-Instanz und gibt diese zurueck
	 * 
	 * @param client
	 *            Der aktuelle Client
	 * @return Eine Instanz der gewaehlten Logik
	 * @throws Exception
	 *             Wenn etwas schief gelaufen ist und keine Instanz erstellt
	 *             werden konnte, wird eine Exception geworfen!
	 */
	public IGameHandler createInstance(AbstractClient client) throws Exception {
		LOGGER.debug("Erzeuge Instanz von: {}", name());
		return builder.build(client);
	}
}
