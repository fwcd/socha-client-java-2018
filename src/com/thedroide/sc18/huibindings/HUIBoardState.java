package com.thedroide.sc18.huibindings;

import java.util.ArrayList;
import java.util.List;

import com.thedroide.sc18.algorithmics.ABoard;
import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;
import com.thedroide.sc18.algorithmics.APlayer;
import com.thedroide.sc18.debug.GUILogger;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

public class HUIBoardState implements ABoardState {
	private ABoard board;
	private GameState state;
	
	public HUIBoardState(GameState state) {
		this.state = state;
		board = HUIBoard.of(state.getBoard());
	}
	
	public GameState getSCState() {
		return state;
	}
	
	@Override
	public ABoard getBoard() {
		return board;
	}

	@Override
	public List<AMove> getPossibleMoves() {
		List<AMove> possibleMoves = new ArrayList<>();
		
		for (Move move : state.getPossibleMoves()) {
			possibleMoves.add(new HUIMove(move));
		}
		
		return possibleMoves;
	}

	@Override
	public APlayer getCurrentPlayer() {
		return HUIPlayer.of(board, state.getCurrentPlayer());
	}

	@Override
	public void switchTurns() {
		GUILogger.log(state.getTurn());
		state.switchCurrentPlayer();
	}

	@Override
	public ABoardState copy() {
		try {
			return new HUIBoardState(state.clone());
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		return "R" + Integer.toString(state.getRedPlayer().getFieldIndex()) + " - "
				+ "B" + Integer.toString(state.getBluePlayer().getFieldIndex());
	}
}
