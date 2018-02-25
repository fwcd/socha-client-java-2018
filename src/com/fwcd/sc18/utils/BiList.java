package com.fwcd.sc18.utils;

/**
 * A two-column list/table.
 * 
 * @author Fredrik
 *
 * @param <A> The first column item type
 * @param <B> The second column item type
 */
public interface BiList<A, B> {
	void add(A a, B b);
	
	void add(int i, A a, B b);
	
	void remove(A a);
	
	void remove(A a, B b);
	
	void remove(int i);
	
	boolean containsA(A a);
	
	boolean containsB(B b);
	
	boolean contains(A a, B b);
	
	int size();
	
	int indexOf(A a);
	
	int indexOf(A a, B b);
	
	void remap(A a, B b);
	
	A getA(int i);
	
	B getB(int i);

	void clear();
}
