package com.thedroide.sc18.utils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A simple LIFO-Stack implementation.
 * 
 * @param <T> - The stack item type
 */
public class Stack<T> {
	private Deque<T> data = new ArrayDeque<>();
	
	public void push(T item) {
		data.push(item);
	}
	
	public T pop() {
		return data.pop();
	}
	
	public T peek() {
		return data.peek();
	}
	
	public int size() {
		return data.size();
	}
	
	public boolean isEmpty() {
		return data.isEmpty();
	}
}
