package fwcd.sc18.agbinds;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.GameState;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

public class AGGameState implements GamePlay {
	private static final long serialVersionUID = -8870083609852496287L;
	private final GameState state;
	private List<GameMove> legalMoves;
	private Deque<GameMove> moveStack = new ArrayDeque<>();
	
	public AGGameState() {
		this(new GameState());
	}
	
	public AGGameState(GameState state) {
		this.state = state;
		legalMoves = listLegalMoves();
	}

	@Override
	public boolean makeMove(GameMove move) {
		try {
			((AGMove) move).get().perform(state);
			moveStack.push(move);
			legalMoves = listLegalMoves();
			return true;
		} catch (InvalidMoveException | InvalidGameStateException e) {
			return false;
		}
	}
	
	public GameState getState() { return state; }
	
	private List<GameMove> listLegalMoves() {
		final AGPlayerColor player = nextPlayerColor();
		return state.getPossibleMoves().stream()
				.map(m -> new AGMove(m, player))
				.collect(Collectors.toList());
	}

	private AGPlayerColor nextPlayerColor() {
		return AGPlayerColor.of(state.getCurrentPlayerColor());
	}

	@Override
	public int[] getWinner() {
		PlayerColor winner = HUIUtils.getWinnerOrNull(state);
		return winner == null ? null : new int[] {AGPlayerColor.of(winner).asRole()};
	}

	@Override
	public double getResult(int playerRole) {
		int[] winner = getWinner();
		return winner == null ? 0 : (winner[0] == playerRole ? 1 : -1);
	}

	@Override
	public AGGameState spawnChild(GameMove move) {
		AGGameState clone = clone();
		clone.makeMove(move);
		return clone;
	}
	
	@Override
	public AGGameState clone() {
		try {
			return new AGGameState(state.clone());
		} catch (CloneNotSupportedException e) {
			throw new GameRuntimeException("Clone not supported: ", e);
		}
	}
	
	@Override
	public String getGameName() { return "Hase und Igel"; }

	@Override
	public int numberOfPlayers() { return 2; }

	@Override
	public int nextPlayer() { return nextPlayerColor().asRole(); }

	@Override
	public GameMove[] getLegalMoves() { return legalMoves.toArray(new GameMove[0]); }

	@Override
	public boolean isLegalMove(GameMove move) { return legalMoves.contains(move); }

	@Override
	public GameMove[] getMoveHistory() { return moveStack.toArray(new GameMove[0]); }

	@Override
	public GameMove[] getRedoList() { return new GameMove[0]; }

	@Override
	public boolean undoLastMove() { return false; }

	@Override
	public boolean redoMove() { return false; }
}
