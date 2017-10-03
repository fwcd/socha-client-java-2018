package com.thedroide.sc18.bindings;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.TemplatePlayer;

import sc.plugin2018.Player;

public class HUIPlayer extends TemplatePlayer {
	private static final long serialVersionUID = -2746100695353269130L;

	private final int carrotWeight = 5;
	private final int saladWeight = 10;
	private final int fieldIndexWeight = 1;
	private final int carrotOptimum = 8; // Might need some tweaks
	
	@Override
	public boolean canPlayGame(GamePlay game) {
		if (game instanceof HUIGamePlay) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role)
			throws CannotPlayGameException, GameRuntimeException {
		try {
			HUIGamePlay huiGame = (HUIGamePlay) game;
//			HUIMove huiMove = (HUIMove) move;
			HUIEnumPlayer huiEnumPlayer = HUIEnumPlayer.of(role);
			Player scPlayer = huiEnumPlayer.getSCPlayer(huiGame.getSCState());
			
			if (scPlayer.inGoal()) {
				return Double.MAX_VALUE; // Obviously a very good rating when the player reaches the goal
			} else {
				int salads = scPlayer.getSalads();
				int carrots = scPlayer.getCarrots();
				int fieldIndex = scPlayer.getFieldIndex();
				
				return (fieldIndex * fieldIndexWeight) // Large field-index: better
						- (salads * saladWeight) // Less salads: better
						- Math.abs((carrots - carrotOptimum) * carrotWeight); // More or less carrots than optimum: worse
			}
		} catch (ClassCastException e) {
			throw new CannotPlayGameException(this, game, "Invalid game type.");
		}
	}
}
