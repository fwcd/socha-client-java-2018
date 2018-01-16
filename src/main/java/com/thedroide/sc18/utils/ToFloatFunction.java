package com.thedroide.sc18.utils;

@FunctionalInterface
public interface ToFloatFunction<T> {
	float apply(T input);
}
