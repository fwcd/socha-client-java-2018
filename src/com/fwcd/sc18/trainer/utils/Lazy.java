package com.fwcd.sc18.trainer.utils;

import java.util.function.Supplier;

public class Lazy<T> {
	private final Supplier<T> getter;
	private T data;
	
	public Lazy(Supplier<T> getter) {
		this.getter = getter;
	}
	
	public T get() {
		if (data == null) {
			data = getter.get();
		}
		
		return data;
	}
}
