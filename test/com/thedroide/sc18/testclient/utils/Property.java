package com.thedroide.sc18.testclient.utils;

import java.util.function.Consumer;

public interface Property<T> {
	T get();
	
	void set(T value);
	
	void addChangeListener(Consumer<T> onChange);
}
