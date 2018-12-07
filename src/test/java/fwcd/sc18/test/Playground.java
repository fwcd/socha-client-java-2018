package fwcd.sc18.test;

import fwcd.sc18.utils.IntSet;

public class Playground {
	public static void main(String[] args) {
		IntSet set = new IntSet();
		System.out.println(set);
		set.add(982798);
		set.add(2);
		set.add(4);
		set.add(2);
		set.add(0);
		set.add(3);
		set.add(0);
		set.add(901);
		set.add(982798);
		set.add(16);
		set.add(32);
		set.add(64);
		set.add(128);
		
		System.out.println(set.bucketsToString());
		System.out.println(set);
		System.out.println(set.contains(1982));
		System.out.println(set.contains(0));
		System.out.println(set.contains(982798));
		
		set.remove(982798);
		
		System.out.println(set);
		System.out.println(set.contains(982798));
	}
}
