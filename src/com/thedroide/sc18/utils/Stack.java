package com.thedroide.sc18.utils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * A simple LIFO-Stack implementation.
 * 
 * @param <T> - The stack item type
 */
public class Stack<T> implements Cloneable {
	private Deque<T> data;
	
	/**
	 * Constructs an empty stack.
	 */
	public Stack() {
		data = new ArrayDeque<>();
	}
	
	/**
	 * Constructs a new stack using the given data.
	 * 
	 * @param data - The items used for the new stack
	 */
	public Stack(Collection<? extends T> data) {
		data = new ArrayDeque<>(data);
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
	@Override
	public Stack<T> clone() {
		return new Stack<>(data);
	}
}
