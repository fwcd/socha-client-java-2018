package com.thedroide.sc18.test.demo.pong;

import com.thedroide.sc18.test.demo.pong.PongGame.Ball;
import com.thedroide.sc18.test.demo.pong.PongGame.Paddle;

@FunctionalInterface
public interface PongPlayer {
	boolean shouldMoveUp(Paddle me, Paddle opponent, Ball ball);
}
