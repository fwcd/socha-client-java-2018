package fwcd.sc18.utils;

import sc.plugin2018.FieldType;
import sc.plugin2018.GameState;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

/**
 * A convenience class to aggregate
 * the resulting score of a match.
 */
public class MatchResult {
	private final GameState state;
	private final Player me;
	private final Player opponent;
	private final boolean won;
	private final GameResult result;
	private final String errorMessage;
	private final float[] genes;
	
	public MatchResult(
			GameState state,
			PlayerColor myColor,
			boolean won,
			GameResult result,
			String errorMessage,
			float[] genes
	) {
		this.state = state;
		this.result = result;
		this.errorMessage = errorMessage;
		this.won = won;
		this.genes = genes;
		me = state.getPlayer(myColor);
		opponent = state.getPlayer(myColor.opponent());
	}
	
	public float[] getGenes() { return genes; }
	
	public GameState getState() { return state; }
	
	public Player getMe() { return me; }
	
	public int getMyCarrots() { return me.getCarrots(); }
	
	public int getMySalads() { return me.getSalads(); }
	
	public int getMyFieldIndex() { return me.getFieldIndex(); }
	
	public FieldType getMyFieldType() { return state.getTypeAt(me.getFieldIndex()); }
	
	public int getTurn() { return state.getTurn(); }
	
	public boolean isWon() { return won; }
	
	public boolean inGoal() { return me.inGoal(); }
	
	public Player getOpponent() { return opponent; }
	
	public int getOpponentCarrots() { return opponent.getCarrots(); }
	
	public int getOpponentFieldIndex() { return opponent.getFieldIndex(); }
	
	public FieldType getOpponentFieldType() { return state.getTypeAt(opponent.getFieldIndex()); }
	
	public String getErrorMessage() { return errorMessage; }
	
	public GameResult getResult() { return result; }
}
