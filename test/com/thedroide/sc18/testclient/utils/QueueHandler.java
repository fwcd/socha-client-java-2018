package com.thedroide.sc18.testclient.utils;

import java.util.Queue;
import java.util.function.Consumer;

public class QueueHandler<T> implements Runnable {
	private final Queue<T> queue;
	private final Consumer<T> handler;
	private final long delay = 200; // ms
	
	public QueueHandler(Queue<T> queue, Consumer<T> handler) {
		this.queue = queue;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		boolean running = true;
		while (running) {
			try {
				if (!queue.isEmpty()) {
					handler.accept(queue.poll());
				}
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}
}
