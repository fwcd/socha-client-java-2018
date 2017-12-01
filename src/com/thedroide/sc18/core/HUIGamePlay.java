package com.thedroide.sc18.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.GameUtilities;
import com.thedroide.sc18.algorithmics.GraphTreeNode;
import com.thedroide.sc18.debug.GUILogger;

import sc.plugin2018.Board;
import sc.plugin2018.Field;
import sc.plugin2018.GameState;
import sc.plugin2018.Player;
import sc.shared.InvalidMoveException;

/**
 * Represents a (mutable) state of the game.<br><br>
 * 
 * Provides the foundation for the Game-API
 * to interact with the "Hase und Igel"-game.
 */
public class HUIGamePlay implements GamePlay {
	private static final long serialVersionUID = -6693551955267419333L;
	
	private GameState state;
	private List<HUIMove> moveHistory = new ArrayList<>();
	private List<HUIMove> legalMoves = null;
	
	/**
	 * Constructs a new empty HUIGamePlay.<br><br>
	 * 
	 * This constructor should
	 * always be used with caution as it might cause NullPointerExceptions
	 * at unexpected places.
	 */
	public HUIGamePlay() {
		
	}
	
	/**
	 * Constructs a new {@link HUIGamePlay} from the
	 * given {@link GameState} (Software Challenge API).
	 * 
	 * @param state - The GameState used as a base
	 */
	public HUIGamePlay(GameState state) {
		setState(state);
	}
	
	public void setState(GameState state) {
		try {
			moveHistory.clear();
			this.state = state.clone();
			updateLegalMoves();
		} catch (CloneNotSupportedException e) {
			GUILogger.printStack(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Fetches the player who has to commit the next
	 * move.
	 * 
	 * @return The next player as a {@link HUIEnumPlayer}.
	 */
	public HUIEnumPlayer nextHUIEnumPlayer() {
		return HUIEnumPlayer.of(state.getCurrentPlayerColor());
	}
	
	/**
	 * Fetches the field of the given player.
	 * 
	 * @param player - The player
	 * @return The current field of that player in this state
	 */
	public Field fieldOf(HUIEnumPlayer player) {
		int index = getSCPlayer(player).getFieldIndex();
		Field field = new Field(state.getBoard().getTypeAt(index));
		field.setIndex(index);
		
		return field;
	}
	
	@Override
	public int nextPlayer() {
		return nextHUIEnumPlayer().getID();
	}

	@Override
	public int[] getWinner() {
		if (state.getRedPlayer().inGoal()) {
			return new int[] {HUIEnumPlayer.RED.getID()};
		} else if (state.getBluePlayer().inGoal()) {
			return new int[] {HUIEnumPlayer.BLUE.getID()};
		} else {
			return null;
		}
	}
	
	public synchronized void sortLegalMoves(Comparator<HUIMove> comparator) {
		Collections.sort(legalMoves, comparator);
	}
	
	/**
	 * Updates the list of legal moves internally. <b>Should
	 * be called whenever the field "state" is updated!!</b>
	 */
	private synchronized void updateLegalMoves() {
		legalMoves = state
				.getPossibleMoves()
				.stream()
				.map(scMove -> new HUIMove(nextHUIEnumPlayer(), scMove))
				.filter(huiMove -> !huiMove.isSkip())
				.collect(Collectors.toList());
	}
	
	/**
	 * Fetches information about a given player.
	 * 
	 * @param player - A player (from the software challenge API)
	 * @return A string representation of the "stats"
	 */
	private String getStats(Player player) {
		return "{"
				+ "F: " + Integer.toString(player.getFieldIndex())
				+ ", S: " + Integer.toString(player.getSalads())
				+ ", C: " + Integer.toString(player.getCarrots())
				+ "}";
	}

	@Override
	public String getGameName() {
		return "HaseUndIgel";
	}

	@Override
	public int numberOfPlayers() {
		return 2;
	}

	@Override
	public synchronized boolean makeMove(GameMove move) {
		try {
			HUIMove huiMove = (HUIMove) move;
			
			// Player isn't allowed to skip move if there are other legal moves
			
			if (huiMove.isSkip() && legalMoves.size() > 1) {
				// TODO: This block of code shouldn't actually be required...
				// but I'm not sure if it's needed or not!
				
				return false;
			}
			
			huiMove.getSCMove().perform(state);
			moveHistory.add(huiMove);
			updateLegalMoves();
			return true;
		} catch (InvalidMoveException e) {
			// This exception is expected to happen, when the hard max time
			// is reached and a ShallowStrategy is used to determine the best
			// move in SmartLogic. Thus it is intended that no stacktrace is
			// printed.
			return false;
		}
	}
	
	/**
	 * Fetches the legal moves as a list.
	 * 
	 * @return A read-only list containing the possible/legal moves
	 */
	public List<HUIMove> getLegalMovesList() {
		return Collections.unmodifiableList(legalMoves);
	}
	
	/**
	 * Fetches the game board.
	 * 
	 * @return The board instance
	 */
	public Board getBoard() {
		return state.getBoard();
	}
	
	@Override
	public GameMove[] getLegalMoves() {
		return legalMoves.toArray(new GameMove[0]);
	}

	@Override
	public boolean isLegalMove(GameMove move) {
		return legalMoves.contains(move);
	}

	@Override
	public GameMove[] getMoveHistory() {
		return moveHistory.toArray(new GameMove[0]);
	}

	@Override
	public GameMove[] getRedoList() {
		return new GameMove[0]; // Due to undo/redo not being supported
	}
	
	@Override
	public boolean undoLastMove() {
		return false; // Not supported
	}

	@Override
	public boolean redoMove() {
		return false; // Not supported
	}
	
	public boolean gameOver() {
		return getLegalMoves().length == 0;
	}

	@Override
	public double getResult(int playerRole) throws GameRuntimeException {
		if (!gameOver()) {
            throw new GameRuntimeException(this, "The game is still in progress and thus doesn't have a result yet!");
        }
        return GameUtilities.checkForWin(this, new int[] {playerRole});
	}

	@Override
	public GamePlay spawnChild(GameMove move) throws GameRuntimeException {
		try {
			HUIGamePlay child = clone();
			child.makeMove(move);
			return child;
		} catch (CloneNotSupportedException e) {
			GUILogger.printStack(e);
			throw new GameRuntimeException("Game state couldn't be cloned.", e);
		}
	}
	
	@Override
	public String toString() {
		return "[Board: R" + getStats(state.getRedPlayer()) + " | " + "B" + getStats(state.getBluePlayer())
				+ " (" + state.getCurrentPlayerColor().toString() + "'s turn)";
	}
	
	@Override
	public HUIGamePlay clone() throws CloneNotSupportedException {
		HUIGamePlay clone = (HUIGamePlay) super.clone();
		clone.state = state.clone();
		clone.moveHistory = new ArrayList<>(moveHistory);

		return clone;
	}
	
	/**
	 * A bridge method between {@link HUIEnumPlayer} and
	 * the associated {@link sc.plugin2018.Player} (Software Challenge API).
	 * 
	 * @param player - The HUIEnumPlayer
	 * @return The Player from the Software Challenge API
	 */
	protected Player getSCPlayer(HUIEnumPlayer player) {
		switch (player) {
		
		case RED:
			return state.getRedPlayer();
		case BLUE:
			return state.getBluePlayer();
		default:
			throw new NoSuchElementException("Couldn't find required player!");
		
		}
	}
}
