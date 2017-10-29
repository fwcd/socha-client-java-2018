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
	// Verfuegbare Taktiken (Implementierungen des IGameHandler) muessen hier
	// eingetragen wie im Beispiel eingetragen und ihre Klasse angegeben werden
	SIMPLE(SimpleLogic.class),

	// Unsere Strategie
	SMART(SmartLogic.class);

	private Class<? extends IGameHandler> logic;
	private static final Logger	LOGGER = LoggerFactory.getLogger(LogicFactory.class);

	private LogicFactory(Class<? extends IGameHandler> chosenLogic) {
		logic = chosenLogic;
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
	public IGameHandler getInstance(AbstractClient client) throws Exception {
		LOGGER.debug("Erzeuge Instanz von: {}", name());
		return (IGameHandler) logic.getConstructor(client.getClass())
				.newInstance(client);
	}

}
