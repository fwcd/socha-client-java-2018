package com.thedroide.sc18.bindings;

import com.antelmann.game.AbstractGame;
import com.antelmann.game.GameMove;
import com.thedroide.sc18.debug.GUILogger;
import com.thedroide.sc18.utils.CopyStack;

import sc.plugin2018.GameState;
import sc.shared.InvalidMoveException;

/**
 * Represents a state of the game.<br><br>
 * 
 * Provides the foundation for the Game-API
 * to interact with the "Hase und Igel"-game.
 */
public class HUIGamePlay extends AbstractGame {
	private static final long serialVersionUID = -6693551955267419333L;
	
	private CopyStack<GameState> states = new CopyStack<>();

	/**
	 * Constructs a new {@link HUIGamePlay} from the
	 * given {@link GameState} (Software Challenge API).
	 * 
	 * @param state - The GameState used as a base
	 */
	public HUIGamePlay(GameState state) {
		super("HaseUndIgel", 2);
		
		states.push(state);
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
	 * Fetches the current player of this state.
	 * 
	 * @return The current {@link HUIEnumPlayer}
	 */
	private HUIEnumPlayer currentPlayer() {
		return HUIEnumPlayer.of(getSCState().getCurrentPlayerColor());
	}
	
	@Override
	public int nextPlayer() {
		return HUIEnumPlayer.of(getSCState().getOtherPlayerColor()).getID();
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
				.map((scMove) -> new HUIMove(currentPlayer(), scMove))
				.toArray((size) -> new GameMove[size]);
	}

	@Override
	protected boolean pushMove(GameMove move) {
		try {
			GameState newState = getSCState().clone();
			GUILogger.log("Before: " + newState.getCurrentPlayer().getPlayerColor() + "R" + newState.getRedPlayer().getFieldIndex() + "B" + newState.getBluePlayer().getFieldIndex());
			((HUIMove) move).getSCMove().perform(newState);
			GUILogger.log("After: " + newState.getCurrentPlayer().getPlayerColor() + "R" + newState.getRedPlayer().getFieldIndex() + "B" + newState.getBluePlayer().getFieldIndex());
			states.push(newState);
			return true;
		} catch (CloneNotSupportedException | InvalidMoveException e) {
			GUILogger.log("Invalid move: " + move + " from " + toString());
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
		
		return "R" + Integer.toString(state.getRedPlayer().getFieldIndex()) + " | "
				+ "B" + Integer.toString(state.getBluePlayer().getFieldIndex())
				+ " (" + state.getCurrentPlayerColor().toString() + "'s turn)";
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

		GUILogger.log(states.size() + " | " + states.equals(clone.states) + " | " + clone.states.size());
		return clone;
	}
}
