package sc.player2018;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jargs.gnu.CmdLineParser;
import sc.shared.SharedConfiguration;

/**
 * Hauptklasse des Clients, die ueber Konsolenargumente gesteuert werden kann.
 * Sie veranlasst eine Verbindung zum Spielserver und waehlt eine Strategie.
 */
public class SochaClientMain {
	private static final Logger LOG = LoggerFactory.getLogger(SochaClientMain.class);
	
	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");

		// Define parameters
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option hostOption = parser.addStringOption('h', "host");
		CmdLineParser.Option portOption = parser.addIntegerOption('p', "port");
		CmdLineParser.Option strategyOption = parser.addStringOption('s', "strategy");
		CmdLineParser.Option reservationOption = parser.addStringOption('r', "reservation");

		try {
			// Parse parameters
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			// Show help if parser fails
			showHelp(e.getMessage());
			System.exit(2);
		}

		// Store parameters
		String host = (String) parser.getOptionValue(hostOption, "localhost");
		int port = (Integer) parser.getOptionValue(portOption, SharedConfiguration.DEFAULT_PORT);
		String reservation = (String) parser.getOptionValue(reservationOption, "");
		String strategy = (String) parser.getOptionValue(strategyOption, "");

		// Create a new client
		try {
			new SochaClient(host, port, reservation, strategy);
		} catch (Exception e) {
			LOG.error("Beim Starten den Clients ist ein Fehler aufgetreten: ", e);
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
