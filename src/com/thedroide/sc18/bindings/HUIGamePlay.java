package com.thedroide.sc18.bindings;

import com.antelmann.game.AbstractGame;
import com.antelmann.game.GameMove;
import com.thedroide.sc18.utils.Stack;

import sc.plugin2018.GameState;

public class HUIGamePlay extends AbstractGame {
	private static final long serialVersionUID = -6693551955267419333L;
	
	private Stack<GameState> states = new Stack<>();
	
	public HUIGamePlay(GameState state) {
		super("HaseUndIgel", 2);
		
		states.push(state);
	}
	
	public GameState getSCState() {
		return states.peek();
	}
	
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
			states.push(newState);
			return true;
		} catch (CloneNotSupportedException e) {
			return false;
		}
	}

	@Override
	protected boolean popMove() {
		if (!states.isEmpty()) {
			states.pop();
			return true;
		} else {
			return false;
		}
	}
}
