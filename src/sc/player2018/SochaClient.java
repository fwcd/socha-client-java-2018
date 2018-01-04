package sc.player2018;

import sc.framework.plugins.SimplePlayer;
import sc.plugin2018.AbstractClient;
import sc.plugin2018.IGameHandler;

public class SochaClient extends AbstractClient {
	public SochaClient(String host, int port, String reservation, String strategy) throws Exception {
		// Launch client
		super(host, port);
		
		// Choose strategy
		IGameHandler logic = LogicFactory.getDefault().createInstance(this);

		setHandler(logic);

		// Join a game
		if (reservation == null || reservation.isEmpty()) {
			joinAnyGame();
		} else {
			joinPreparedGame(reservation);
		}

	}

	@Override
	public void onGamePaused(String roomId, SimplePlayer nextPlayer) {
		
	}

	@Override
	public void onGameObserved(String roomId) {
		// is called when a observation request is acknowledged by the server
		// this is a newly added method, I am not sure if it fits into the architecture
	}
}
