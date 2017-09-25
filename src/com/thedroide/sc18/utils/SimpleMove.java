package com.thedroide.sc18.utils;

import sc.plugin2018.Field;
import sc.plugin2018.FieldType;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * A Move wrapper simplifying API calls
 */
public class SimpleMove {
	private GameState stateAfterMove;
	private Move move;
	
	/**
	 * Constructs a new SimpleMove.
	 * 
	 * @param stateAfterMove - The game state after the move but with the current player set to the person who committed the move
	 * @param move - The commited move
	 */
	public SimpleMove(GameState stateAfterMove, Move move) {
		this.stateAfterMove = stateAfterMove;
		this.move = move;
	}
	
	public Field getDestination() {
		FieldType type = stateAfterMove.fieldOfCurrentPlayer();
		int index = stateAfterMove.getCurrentPlayer().getFieldIndex();
		Field field = new Field(type);
		field.setIndex(index);
		
		return field;
	}
	
	public Move getSCMove() {
		return move;
	}
}
