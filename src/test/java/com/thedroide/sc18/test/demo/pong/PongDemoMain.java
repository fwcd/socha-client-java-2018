package com.thedroide.sc18.test.demo.pong;

public class PongDemoMain {
	public static void main(String[] args) {
		new PongGame(new RandomPongPlayer(), new RandomPongPlayer());
	}
}
