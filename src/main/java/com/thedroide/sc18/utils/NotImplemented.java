package com.thedroide.sc18.utils;

public @interface NotImplemented {
	/**
	 * Whether the annotated element was "intentionally"
	 * not implemented (e.g. not implementing the method was
	 * a design choice that is unlikely to change).
	 */
	boolean intentionally();
}
