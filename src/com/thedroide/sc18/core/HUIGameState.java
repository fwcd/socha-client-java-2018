package com.thedroide.sc18.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.GameUtilities;
import com.thedroide.sc18.cache.GameChildCache;
import com.thedroide.sc18.utils.NotImplemented;
import com.thedroide.sc18.utils.TreeNode;

import sc.plugin2018.Board;
import sc.plugin2018.Field;
import sc.plugin2018.GameState;
import sc.plugin2018.Player;
import sc.plugin2018.util.Constants;
import sc.shared.InvalidMoveException;

/**
 * Represents an immutable state of the game. (Technically
 * speaking, legal moves can still be rearranged but that
 * doesn't affect hashCode() or equals())<br><br>
 * 
 * Provides the foundation for the Game-API
 * to interact with the "Hase und Igel"-game.
 */
public class HUIGameState implements GamePlay, TreeNode {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	private static final long serialVersionUID = -6693551955267419333L;
	private static final GameChildCache CACHE = new GameChildCache(200);
	
	private GameState state;
	private List<HUIMove> legalMoves = Collections.emptyList();
	
	/**
	 * Constructs a new (completely) empty HUIGamePlay.
	 */
	private HUIGameState() {
	}
	
	/**
	 * Constructs a new {@link HUIGameState} from the
	 * given {@link GameState} (Software Challenge API).
	 * 
	 * @param state - The GameState used as a base
	 */
	public HUIGameState(GameState state) {
		try {
			this.state = state.clone();
			updateLegalMoves();
		} catch (CloneNotSupportedException e) {
			LOG.error("Could not clone game state: ", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Rearranges the legal moves by pushing
	 * the provided move to the start of the array (index 0). This
	 * might increase performance of alpha-beta.
	 * 
	 * @param move - The (legal) move to be pushed to the beginning of the move list
	 * @param i - The index of the given move in the legal moves list (for performance)
	 * @throws IllegalArgumentException if the move is not legal or the index is wrong
	 */
	public void pushLegalMove(HUIMove move, int i) {
		if (legalMoves.get(i).equals(move)) {
			Collections.swap(legalMoves, 0, i);
		}
		
		throw new IllegalArgumentException("Move " + move.toString() + " does not match legal move at" + Integer.toString(i));
	}
	
	/**
	 * Fetches the player who has to commit the next
	 * move.
	 * 
	 * @return The next player as a {@link HUIPlayerColor}.
	 */
	public HUIPlayerColor nextHUIEnumPlayer() {
		return HUIPlayerColor.of(state.getCurrentPlayerColor());
	}
	
	/**
	 * Fetches the field of the given player.
	 * 
	 * @param player - The player
	 * @return The current field of that player in this state
	 */
	public Field fieldOf(HUIPlayerColor player) {
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
			return new int[] {HUIPlayerColor.RED.getID()};
		} else if (state.getBluePlayer().inGoal()) {
			return new int[] {HUIPlayerColor.BLUE.getID()};
		} else {
			return null;
		}
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
				.collect(Collectors.collectingAndThen(
						Collectors.toList(),
						Collections::unmodifiableList
				));
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

	/**
	 * Not supported due to immutability and thus always
	 * returning false.
	 */
	@Override
	@NotImplemented(intentionally = true)
	@Deprecated
	public boolean makeMove(GameMove move) {
		throw new UnsupportedOperationException("Tried to call unsupported method makeMove(...)!");
	}
	
	/**
	 * Not supported due to immutability and thus always
	 * returning false.
	 */
	@Override
	@NotImplemented(intentionally = true)
	@Deprecated
	public boolean undoLastMove() {
		LOG.debug("Tried to call unsupported method undoLastMove()!");
		return false;
	}

	/**
	 * Not supported due to immutability and thus always
	 * returning false.
	 */
	@Override
	@NotImplemented(intentionally = true)
	@Deprecated
	public boolean redoMove() {
		LOG.debug("Tried to call unsupported method redoMove()!");
		return false;
	}
	
	/**
	 * Internal method that mutates this state to advance to
	 * the next move. <b>Should ONLY be called during initialization
	 * to guarantee immutability!!</b>
	 * 
	 * @param move - The move to be performed
	 * @return Whether the move has been applied successfully
	 */
	private boolean perform(GameMove move) {
		if (move == null) {
			return false;
		}
		
		try {
			HUIMove huiMove = (HUIMove) move;
			
			// Player isn't allowed to skip move if there are other legal moves
			
			if (huiMove.isSkip() && legalMoves.size() > 1) {
				// TODO: This block of code shouldn't actually be required...
				// but I'm not sure if it's needed or not!
				
				return false;
			}
			
			huiMove.getSCMove().perform(state);
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
		return legalMoves;
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
	public HUIMove[] getLegalMoves() {
		return legalMoves.toArray(new HUIMove[0]);
	}

	@Override
	public boolean isLegalMove(GameMove move) {
		return legalMoves.contains(move);
	}

	/**
	 * Unsupported due to performance.
	 */
	@Override
	@NotImplemented(intentionally = false)
	public HUIMove[] getMoveHistory() {
		return new HUIMove[0];
	}

	@Override
	@NotImplemented(intentionally = true)
	public GameMove[] getRedoList() {
		return new GameMove[0]; // Due to undo/redo not being supported
	}
	
	public boolean isGameOver() {
		return getLegalMoves().length == 0;
	}

	@Override
	public double getResult(int playerRole) {
		if (!isGameOver()) {
            throw new GameRuntimeException(this, "The game is still in progress and thus doesn't have a result yet!");
        }
        return GameUtilities.checkForWin(this, new int[] {playerRole});
	}
	
	@Override
	public HUIGameState spawnChild(GameMove move) {
		HUIMove huiMove = (HUIMove) move;
		return CACHE.getOrStoreChild(this, huiMove, () -> createChild(move));
	}

	private HUIGameState createChild(GameMove move) {
		HUIGameState child = clone();
		child.perform(move);
		return child;
	}
	
	@Override
	public String toString() {
		return "[Board: R" + getStats(state.getRedPlayer()) + " | " + "B" + getStats(state.getBluePlayer())
				+ " (" + state.getCurrentPlayerColor().toString() + "'s turn)";
	}
	
	/**
	 * Clones this game state. <b>Public access to this method is
	 * almost always redundant, because HUIGameState instances
	 * themselves already are immutable.</b>
	 */
	@Override
	public HUIGameState clone() {
		try {
			HUIGameState clone = new HUIGameState();
			
			clone.state = state.clone();
			clone.legalMoves = new ArrayList<>(legalMoves);

			return clone;
		} catch (CloneNotSupportedException e) {
			LOG.error("Could not clone game state: ", e);
			throw new GameRuntimeException("Game state couldn't be cloned.", e);
		}
	}
	
	/**
	 * A bridge method between {@link HUIPlayerColor} and
	 * the associated {@link sc.plugin2018.Player} (Software Challenge API).
	 * 
	 * @param player - The HUIEnumPlayer
	 * @return The Player from the Software Challenge API
	 */
	protected Player getSCPlayer(HUIPlayerColor player) {
		switch (player) {
		
		case RED:
			return state.getRedPlayer();
		case BLUE:
			return state.getBluePlayer();
		default:
			throw new NoSuchElementException("Couldn't find required player!");
		
		}
	}

	@Override
	public List<? extends TreeNode> getChildren() {
		return legalMoves.stream()
				.map(this::spawnChild)
				.collect(Collectors.toList());
	}

	@Override
	public String getNodeDescription() {
		return "R" + Integer.toString(getSCPlayer(HUIPlayerColor.RED).getFieldIndex())
				+ "|B" + Integer.toString(getSCPlayer(HUIPlayerColor.BLUE).getFieldIndex());
	}

	@Override
	public boolean isLeaf() {
		return getWinner() != null;
	}
	
	public String getASCII() {
		String s = "";
		int redField = getSCPlayer(HUIPlayerColor.RED).getFieldIndex();
		int blueField = getSCPlayer(HUIPlayerColor.BLUE).getFieldIndex();
		
		for (int i=0; i<Constants.NUM_FIELDS; i++) {
			if (i == redField) {
				s += "R";
			} else if (i == blueField) {
				s += "B";
			} else {
				s += "-";
			}
		}
		
		return s;
	}

	@Override
	public int hashCode() {
		return (state.getTurn()
				+ state.getStartPlayerColor().hashCode()
				+ state.getCurrentPlayerColor().hashCode()) * 31;
	}

	@Override
	public boolean equals(Object obj) {
		HUIGameState other = (HUIGameState) obj;
		return state.getTurn() == other.state.getTurn()
				&& state.getStartPlayerColor().equals(other.state.getStartPlayerColor())
				&& state.getCurrentPlayerColor().equals(other.state.getCurrentPlayerColor());
	}
}
