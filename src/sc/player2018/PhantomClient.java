package sc.player2018;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.fwcd.sc18.geneticneural.GeneticNeuralLogic;

import sc.framework.plugins.SimplePlayer;
import sc.plugin2018.AbstractClient;
import sc.plugin2018.IGameHandler;
import sc.shared.SharedConfiguration;

public class PhantomClient extends AbstractClient {
	private final String reservation;
	private final String strategy;
	
	private boolean autoRelaunch = true; // FIXME: Might want to add accessors
	
	public PhantomClient() throws IOException {
		this("localhost", SharedConfiguration.DEFAULT_PORT, "", "");
	}
	
	public PhantomClient(String host, int port, String reservation, String strategy) throws IOException {
		// Launch client
		super(host, port);
		this.reservation = reservation;
		this.strategy = strategy;
		
		// Choose strategy
		IGameHandler logic;
		if (strategy == null || strategy.isEmpty()) {
			logic = new GeneticNeuralLogic(this);
		} else {
			try {
				logic = (IGameHandler) Class.forName(strategy).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}

		setHandler(logic);

		// Join a game
		if (reservation == null || reservation.isEmpty()) {
			joinAnyGame();
		} else {
			joinPreparedGame(reservation);
		}
	}
	
	public void relaunch() {
		new Thread(() -> {
			try {
				new PhantomClient(getHost(), getPort(), reservation, strategy);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}).start();
	}

	@Override
	public void onGamePaused(String roomId, SimplePlayer nextPlayer) {
		
	}

	@Override
	public void onGameObserved(String roomId) {
		// is called when a observation request is acknowledged by the server
		// this is a newly added method, I am not sure if it fits into the architecture
	}

	@Override
	public void onGameLeft(String roomId) {
		super.onGameLeft(roomId);
		if (autoRelaunch) {
			relaunch();
		}
	}
}
