package sc.player2018;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.UnknownOptionException;
import sc.shared.SharedConfiguration;

/**
 * Hauptklasse des Clients, die ueber Konsolenargumente gesteuert werden kann.
 * Sie veranlasst eine Verbindung zum Spielserver und waehlt eine Strategie.
 */
public class SochaClientMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(SochaClientMain.class);
	
	public static void main(String[] args) throws IllegalOptionValueException, UnknownOptionException, IOException {
		System.setProperty("file.encoding", "UTF-8");

		// XXX only for testing
		// you may use this code to enable debug output:
		Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		((ch.qos.logback.classic.Logger) rootLogger).setLevel(ch.qos.logback.classic.Level.WARN);
		Logger simpleLogicLogger = LoggerFactory.getLogger(SimpleLogic.class);
		((ch.qos.logback.classic.Logger) simpleLogicLogger).setLevel(ch.qos.logback.classic.Level.WARN);

		// parameter definieren
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
		CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
		CmdLineParser.Option strategyOption = parser.addStringOption('s', "strategy");
		CmdLineParser.Option reservationOption = parser.addStringOption('r', "reservation");

		try {
			// Parameter auslesen
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) { // Bei Fehler die Hilfe
			// anzeigen
			showHelp(e.getMessage());
			System.exit(2);
		}

		// Parameter laden
		String host = (String) parser.getOptionValue(hostOption, "localhost");
		int port = (Integer) parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_PORT);
		String reservation = (String) parser.getOptionValue(reservationOption, "");
		String strategy = (String) parser.getOptionValue(strategyOption, "");

		// einen neuen client erzeugen
		try {
			new SochaClient(host, port, reservation, strategy);
		} catch (Exception e) {
			LOGGER.error("Beim Starten den Clients ist ein Fehler aufgetreten:");
			e.printStackTrace();
		}

	}

	private static void showHelp(String errorMsg) {
		System.out.println();
		System.out.println(errorMsg);
		System.out.println();
		System.out.println("Bitte das Programm mit folgenden Parametern (optional) aufrufen: \n"
				+ "java -jar sochaclient.jar [{-h,--host} hostname]\n"
				+ "                               [{-p,--port} port]\n"
				+ "                               [{-r,--reservation} reservierung]\n"
				+ "                               [{-s,--strategy} strategie]");
		System.out.println();
		System.out.println("Beispiel: \n"
				+ "java -jar sochaclient.jar --host 127.0.0.1 --port 10500 --reservation MQ --strategy RANDOM");
		System.out.println();
	}
}
