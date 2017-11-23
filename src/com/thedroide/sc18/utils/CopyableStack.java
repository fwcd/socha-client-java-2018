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
public class CopyableStack<T> implements Stack<T> {
	private Deque<T> data = new ArrayDeque<>();
	
	/**
	 * Constructs an empty stack.
	 */
	public CopyableStack() {
		
	}
	
	/**
	 * Constructs a new stack from the given data.
	 * 
	 * @param other
	 */
	private CopyableStack(Iterable<? extends T> other, UnaryOperator<T> copier) {
		for (T item : other) {
			data.add(copier.apply(item));
		}
	}

	/**
	 * Pushes a new item onto the stack.
	 * 
	 * @param item - The item to be added
	 */
	@Override
	public void push(T item) {
		data.push(item);
	}
	
	/**
	 * Pops an item from the stack.
	 * 
	 * @return The removed item.
	 */
	@Override
	public T pop() {
		return data.pop();
	}
	
	/**
	 * Fetches the top-most item from the stack.
	 * 
	 * @return The top-most item from the stack
	 */
	@Override
	public T peek() {
		return data.peek();
	}
	
	/**
	 * Fetches the size of this stack.
	 * 
	 * @return The amount of items held by this stack
	 */
	@Override
	public int size() {
		return data.size();
	}
	
	/**
	 * Checks if this stack is empty.
	 * 
	 * @return Whether this stack is empty
	 */
	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	@Override
	public void clear() {
		data.clear();
	}
	
	/**
	 * Clears the stack and places a new item in it.
	 * 
	 * @param base - The only item in the stack after rebasing
	 */
	@Override
	public void rebase(T base) {
		clear();
		push(base);
	}
	
	/**
	 * Copies this stack.
	 */
	public CopyableStack<T> copy(UnaryOperator<T> itemCopier) {
		return new CopyableStack<T>(data, itemCopier);
	}
	
	@Override
	public String toString() {
		return data.toString();
	}
}
