package fwcd.sc18.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ArrayBiList<A, B> implements BiList<A, B> {
	private final List<A> aItems = new ArrayList<>();
	private final List<B> bItems = new ArrayList<>();
	
	@Override
	public void add(A a, B b) {
		aItems.add(a);
		bItems.add(b);
	}

	@Override
	public void remove(int i) {
		aItems.remove(i);
		bItems.remove(i);
	}

	@Override
	public int size() {
		int aSize = aItems.size();
		int bSize = bItems.size();
		
		if (aSize != bSize) {
			throw new IllegalStateException("Both list column need to have the same length!");
		} else {
			return aSize;
		}
	}

	@Override
	public A getA(int i) {
		return aItems.get(i);
	}

	@Override
	public B getB(int i) {
		return bItems.get(i);
	}

	@Override
	public boolean contains(A a, B b) {
		return aItems.contains(a) && bItems.contains(b);
	}

	@Override
	public boolean containsA(A a) {
		return aItems.contains(a);
	}

	@Override
	public boolean containsB(B b) {
		return bItems.contains(b);
	}
	
	@Override
	public void remove(A a) {
		final int index = aItems.indexOf(a);
		remove(index);
	}

	@Override
	public void remove(A a, B b) {
		int size = size();
		for (int i=0; i<size; i++) {
			if (aItems.get(i).equals(a) && bItems.get(i).equals(b)) {
				aItems.remove(i);
				bItems.remove(i);
			}
		}
	}

	@Override
	public int indexOf(A a) {
		return aItems.indexOf(a);
	}

	@Override
	public int indexOf(A a, B b) {
		int size = size();
		for (int i=0; i<size; i++) {
			if (aItems.get(i).equals(a) && bItems.get(i).equals(b)) {
				return i;
			}
		}
		
		throw new NoSuchElementException("Pair (" + a.toString() + ", " + b.toString() + ") is not contained by the BiList!");
	}

	@Override
	public void add(int i, A a, B b) {
		aItems.add(i, a);
		bItems.add(i, b);
	}

	@Override
	public void remap(A a, B b) {
		bItems.set(aItems.indexOf(a), b);
	}

	@Override
	public void clear() {
		aItems.clear();
		bItems.clear();
	}
}
