package com.thedroide.sc18.bindings;

import com.antelmann.game.AbstractGame;
import com.antelmann.game.GameMove;
import com.thedroide.sc18.debug.GUILogger;
import com.thedroide.sc18.utils.CopyableStack;

import sc.plugin2018.Field;
import sc.plugin2018.GameState;
import sc.plugin2018.Player;
import sc.shared.InvalidMoveException;

/**
 * Represents a state of the game.<br><br>
 * 
 * Provides the foundation for the Game-API
 * to interact with the "Hase und Igel"-game.
 */
public class HUIGamePlay extends AbstractGame {
	private static final long serialVersionUID = -6693551955267419333L;
	
	private CopyableStack<GameState> states = new CopyableStack<>();

	/**
	 * Constructs a new empty {@link HUIGamePlay}.
	 */
	public HUIGamePlay() {
		super("HaseUndIgel", 2);
	}

	/**
	 * Constructs a new {@link HUIGamePlay} from the
	 * given {@link GameState} (Software Challenge API).
	 * 
	 * @param state - The GameState used as a base
	 */
	public HUIGamePlay(GameState state) {
		this();
		pushSCState(state);
	}

	/**
	 * Pushes a new {@link GameState} onto the internal
	 * state history stack. Note that this is NOT the
	 * move stack in {@link AbstractGame}.
	 * 
	 * @param state
	 */
	private void pushSCState(GameState state) {
		states.push(state);
	}

	/**
	 * Discards all previous states and replaces it
	 * with the given new state.
	 * 
	 * @param gameState - The new game state
	 */
	public void setSCState(GameState gameState) {
		states.rebase(gameState);
	}
	
	/**
	 * Fetches the current {@link GameState} (Software Challenge API)
	 * associated with this {@link HUIGamePlay}.
	 * 
	 * @return The top-most/newest/current GameState from the stack
	 */
	public GameState getSCState() {
		return states.peek();
	}
	
	/**
	 * Fetches the player who has to commit the next
	 * move.
	 * 
	 * @return The next player as a {@link HUIEnumPlayer}.
	 */
	public HUIEnumPlayer nextHUIEnumPlayer() {
		return HUIEnumPlayer.of(getSCState().getCurrentPlayerColor());
	}
	
	/**
	 * Fetches the field of the given player.
	 * 
	 * @param player - The player
	 * @return The current field of that player in this state
	 */
	public Field fieldOf(HUIEnumPlayer player) {
		int index = player.getSCPlayer(getSCState()).getFieldIndex();
		Field field = new Field(getSCState().getBoard().getTypeAt(index));
		field.setIndex(index);
		
		return field;
	}
	
	@Override
	public int nextPlayer() {
		return nextHUIEnumPlayer().getID();
	}

	@Override
	public int[] getWinner() {
		if (getSCState().getRedPlayer().inGoal()) {
			return new int[] {HUIEnumPlayer.RED.getID()};
		} else if (getSCState().getBluePlayer().inGoal()) {
			return new int[] {HUIEnumPlayer.BLUE.getID()};
		} else {
			return null;
		}
	}

	@Override
	protected GameMove[] listLegalMoves() {
		return getSCState()
				.getPossibleMoves()
				.stream()
				.map((scMove) -> new HUIMove(nextHUIEnumPlayer(), scMove))
				.filter((huiMove) -> !huiMove.isSkip())
				.toArray((size) -> new GameMove[size]);
	}

	@Override
	protected boolean pushMove(GameMove move) {
		GameState newState = null;
		
		try {
			HUIMove huiMove = (HUIMove) move;
			
			// Player isn't allowed to skip move if there are other legal moves
			
			if (huiMove.isSkip() && listLegalMoves().length > 1) {
				return false;
			}
			
			// FIXME: Unfinished
			
			newState = getSCState().clone();
			huiMove.getSCMove().perform(newState);
			pushSCState(newState);
			return true;
		} catch (CloneNotSupportedException e) {
			GUILogger.println("Invalid clone:");
			GUILogger.printStack(e);
			return false;
		} catch (InvalidMoveException e) {
			GUILogger.println("Invalid move: " + move + " - legal moves: " + newState.getPossibleMoves().toString());
			GUILogger.printStack(e);
			return false;
		}
	}

	@Override
	protected boolean popMove() {
		if (states.size() > 1) {
			states.pop();
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		GameState state = getSCState();
		
		return "[Board: R" + getStats(state.getRedPlayer()) + " | " + "B" + getStats(state.getBluePlayer())
				+ " (" + state.getCurrentPlayerColor().toString() + "'s turn)";
	}
	
	private String getStats(Player player) {
		return "{"
				+ "F: " + Integer.toString(player.getFieldIndex())
				+ ", S: " + Integer.toString(player.getSalads())
				+ ", C: " + Integer.toString(player.getCarrots())
				+ "}";
	}
	
	@Override
	public HUIGamePlay clone() throws CloneNotSupportedException {
		HUIGamePlay clone = (HUIGamePlay) super.clone();
		clone.states = states.copy((original) -> {
			try {
				return (GameState) original.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		});

		return clone;
	}
}
