package com.thedroide.sc18.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.UnaryOperator;

/**
 * A LIFO-Stack implementation that can be
 * (deeply) copied by passing an item copier.
 * 
 * @param <T> - The stack item type
 */
public class CopyStack<T> {
	private Deque<T> data = new ArrayDeque<>();
	
	/**
	 * Constructs an empty stack.
	 */
	public CopyStack() {
		
	}
	
	/**
	 * Constructs a new stack from the given data.
	 * 
	 * @param other
	 */
	private CopyStack(Iterable<? extends T> other, UnaryOperator<T> copier) {
		for (T item : other) {
			data.add(copier.apply(item));
		}
	}

	/**
	 * Pushes a new item onto the stack.
	 * 
	 * @param item - The item to be added
	 */
	public void push(T item) {
		data.push(item);
	}
	
	/**
	 * Pops an item from the stack.
	 * 
	 * @return The removed item.
	 */
	public T pop() {
		return data.pop();
	}
	
	/**
	 * Fetches the top-most item from the stack.
	 * 
	 * @return The top-most item from the stack
	 */
	public T peek() {
		return data.peek();
	}
	
	/**
	 * Fetches the size of this stack.
	 * 
	 * @return The amount of items held by this stack
	 */
	public int size() {
		return data.size();
	}
	
	/**
	 * Checks if this stack is empty.
	 * 
	 * @return Whether this stack is empty
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	/**
	 * Copies this stack.
	 */
	public CopyStack<T> copy(UnaryOperator<T> itemCopier) {
		return new CopyStack<T>(data, itemCopier);
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}
