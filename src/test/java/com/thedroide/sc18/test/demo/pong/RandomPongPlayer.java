package com.thedroide.sc18.test.demo.pong;

import java.util.concurrent.ThreadLocalRandom;

import com.thedroide.sc18.test.demo.pong.PongGame.Ball;
import com.thedroide.sc18.test.demo.pong.PongGame.Paddle;

public class RandomPongPlayer implements PongPlayer {
	@Override
	public boolean shouldMoveUp(Paddle me, Paddle opponent, Ball ball) {
		return ThreadLocalRandom.current().nextBoolean();
	}
}
