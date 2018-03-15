package com.fwcd.sc18.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventListPoller<E> implements Runnable {
	private final Supplier<Collection<E>> supplier;
	private final Consumer<E> handler;
	
	private int intervalMs = 5000;
	private volatile boolean running = false;
	
	public EventListPoller(Supplier<Collection<E>> supplier, Consumer<E> handler) {
		this.supplier = supplier;
		this.handler = handler;
	}
	
	public void setRefreshInterval(int ms) {
		intervalMs = ms;
	}
	
	@Override
	public void run() {
		running = true;
		while (running && !Thread.interrupted()) {
			Iterator<E> items = supplier.get().iterator();
			
			while (items.hasNext()) {
				handler.accept(items.next());
				items.remove();
			}
			
			try {
				Thread.sleep(intervalMs);
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}
	
	public void stop() {
		running = false;
	}
}
