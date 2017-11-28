package com.thedroide.sc18.utils;

/**
 * Represents a stack.
 * 
 * @param <T> - The item type
 */
public interface Stack<T> {
	void push(T item);
	
	T pop();
	
	T peek();
	
	int size();
	
	boolean isEmpty();
	
	void clear();
	
	void rebase(T base);
	
	<E> E[] toArray(E[] array);
}
