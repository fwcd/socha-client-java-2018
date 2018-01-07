package com.thedroide.sc18.testclient.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class DefaultProperty<T> implements Property<T> {
	private final List<Consumer<T>> changeListeners = new ArrayList<>(5);
	private T value; // Nullable

	public DefaultProperty(T value) {
		set(value);
	}
	
	@Override
	public T get() {
		if (value == null) {
			throw new NoSuchElementException();
		}
		
		return value;
	}

	@Override
	public void set(T value) {
		fireListeners();
		this.value = value;
	}
	
	private void fireListeners() {
		for (Consumer<T> listener : changeListeners) {
			listener.accept(value);
		}
	}

	@Override
	public void addChangeListener(Consumer<T> onChange) {
		changeListeners.add(onChange);
	}
}
