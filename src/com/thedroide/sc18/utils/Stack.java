package com.thedroide.sc18.utils;

/**
 * Represents a stack.
 * 
 * @param <T> - The item type
 */
public interface Stack<T> {
	public void push(T item);
	
	public T pop();
	
	public T peek();
	
	public int size();
	
	public boolean isEmpty();
	
	public void clear();
	
	public void rebase(T base);
	
	public <E> E[] toArray(E[] array);
}
