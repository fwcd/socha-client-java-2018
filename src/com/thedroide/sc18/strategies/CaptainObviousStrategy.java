package com.thedroide.sc18.strategies;

import java.util.Optional;

import com.thedroide.sc18.algorithmics.ABoardState;
import com.thedroide.sc18.algorithmics.AMove;
import com.thedroide.sc18.algorithmics.APlayer;
import com.thedroide.sc18.algorithmics.Strategy;

import sc.plugin2018.FieldType;

public class CaptainObviousStrategy implements Strategy {
	@Override
	public Optional<AMove> perform(ABoardState state) {
		int initialSalads = state.getCurrentPlayer().getSalads();
		
		for (AMove move : state.getPossibleMoves()) {
			ABoardState nextState = state.copy();
			move.performOn(nextState);
			
			APlayer player = nextState.getCurrentPlayer();
			
			boolean playerWinning = player.getField().getType() == FieldType.GOAL;
			boolean eatingSalad = player.getSalads() < initialSalads;
			
			if (playerWinning || eatingSalad) {
				return Optional.of(move);
			}
		}
		
		return Optional.empty();
	}
}
