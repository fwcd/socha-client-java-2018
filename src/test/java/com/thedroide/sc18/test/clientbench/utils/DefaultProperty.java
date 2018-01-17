package com.thedroide.sc18.test.clientbench.utils;

public class DefaultProperty<T> implements Property<T> {
	private T value;

	public DefaultProperty(T value) {
		set(value);
	}
	
	@Override
	public T get() {
		return value;
	}

	@Override
	public void set(T value) {
		this.value = value;
	}
}
