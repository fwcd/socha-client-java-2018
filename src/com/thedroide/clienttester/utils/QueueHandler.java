package com.thedroide.clienttester.utils;

import java.util.Queue;
import java.util.function.Consumer;

/**
 * A queue handler that continuously dequeues items
 * from a queue. Users may wish to run this thread on
 * a seperate thread.
 * 
 * @param <T> - The item data type
 */
public class QueueHandler<T> implements Runnable {
	private final Queue<T> queue;
	private final Consumer<T> handler;

	public QueueHandler(Queue<T> queue, Consumer<T> handler) {
		this.queue = queue;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		while (true) {
			while (queue.isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					return;
				}
			}
			
			handler.accept(queue.poll());
		}
	}
}
