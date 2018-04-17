package sc.player2018;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.trainer.core.VirtualClient;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.Action;
import sc.plugin2018.Advance;
import sc.plugin2018.Card;
import sc.plugin2018.CardType;
import sc.plugin2018.ExchangeCarrots;
import sc.plugin2018.FallBack;
import sc.plugin2018.FieldType;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.plugin2018.util.Constants;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

/**
 * Das Herz des Simpleclients: Eine sehr simple Logik, die ihre Zuege zufaellig
 * waehlt, aber gueltige Zuege macht. Ausserdem werden zum Spielverlauf
 * Konsolenausgaben gemacht.
 */
public class SimpleLogic implements IGameHandler {
	private VirtualClient virtualClient;
	private AbstractClient client;
	private GameState gameState;
	private Player currentPlayer;

	private static final Logger LOG = LoggerFactory.getLogger(SimpleLogic.class);
	private static final Random RANDOM = new SecureRandom();

	public SimpleLogic(VirtualClient client) {
		virtualClient = client;
	}
	
	public SimpleLogic(AbstractClient client) {
		this.client = client;
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {
		LOG.info("The game has ended.");
	}

	@Override
	public void onRequestAction() {
		long startTime = System.currentTimeMillis();
		LOG.debug("A move has been requested.");
		Move move = chooseMove();
		move.orderActions();
		LOG.debug("Sending move: {}", move);
		sendAction(move);
		LOG.warn("Time needed for turn: {} ms", System.currentTimeMillis() - startTime);
	}

	private Move chooseMove() {
		List<Move> possibleMove = gameState.getPossibleMoves(); // Contains at least one entry
		List<Move> saladMoves = new ArrayList<>();
		List<Move> winningMoves = new ArrayList<>();
		List<Move> selectedMoves = new ArrayList<>();

		int index = currentPlayer.getFieldIndex();
		for (Move move : possibleMove) {
			for (Action action : move.actions) {
				if (action instanceof Advance) {
					Advance advance = (Advance) action;
					if (advance.getDistance() + index == Constants.NUM_FIELDS - 1) {
						// Enter goal
						winningMoves.add(move);

					} else if (gameState.getBoard().getTypeAt(advance.getDistance() + index) == FieldType.SALAD) {
						// Move to salad field
						saladMoves.add(move);
					} else {
						// Advance if possible
						selectedMoves.add(move);
					}
				} else if (action instanceof Card) {
					Card card = (Card) action;
					if (card.getType() == CardType.EAT_SALAD) {
						// Advance to hare field and eat salad
						saladMoves.add(move);
					}
				} else if (action instanceof ExchangeCarrots) {
					ExchangeCarrots exchangeCarrots = (ExchangeCarrots) action;
					if (exchangeCarrots.getValue() == 10 && currentPlayer.getCarrots() < 30 && index < 40
							&& !(currentPlayer.getLastNonSkipAction() instanceof ExchangeCarrots)) {
						// Only pick up carrots if there are less than 30,
						// just at the beginning and not twice in a row
						selectedMoves.add(move);
					} else if (exchangeCarrots.getValue() == -10 && currentPlayer.getCarrots() > 30 && index >= 40) {
						// Only drop carrots if at the end
						selectedMoves.add(move);
					}
				} else if (action instanceof FallBack) {
					if (index > 56 /* Last salad field */ && currentPlayer.getSalads() > 0) {
						// Just fall back at the end if we still have salads
						selectedMoves.add(move);
					} else if (index <= 56 && index - gameState.getPreviousFieldByType(FieldType.HEDGEHOG, index) < 5) {
						// Don't pick up too many carrots while falling back
						selectedMoves.add(move);
					}
				} else {
					// Add "eat salad" or "skip"
					selectedMoves.add(move);
				}
			}
		}

		Move move;
		
		if (!winningMoves.isEmpty()) {
			move = winningMoves.get(RANDOM.nextInt(winningMoves.size()));
		} else if (!saladMoves.isEmpty()) {
			move = saladMoves.get(RANDOM.nextInt(saladMoves.size()));
		} else if (!selectedMoves.isEmpty()) {
			move = selectedMoves.get(RANDOM.nextInt(selectedMoves.size()));
		} else {
			move = possibleMove.get(RANDOM.nextInt(possibleMove.size()));
		}

		return move;
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;
		LOG.info("Player's turn: {}", player.getPlayerColor());
	}

	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
		LOG.info("Move: {}", gameState.getTurn());
		LOG.info("Current player: {}", currentPlayer.getPlayerColor());
	}

	@Override
	public void sendAction(Move move) {
		if (client != null) {
			client.sendMove(move);
		} else {
			virtualClient.sendMove(move);
		}
	}
}
