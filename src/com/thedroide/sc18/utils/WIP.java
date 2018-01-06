package com.thedroide.sc18.utils;

/**
 * Marks something as work-in-progress.
 */
public @interface WIP {
	/**
	 * @return Whether the marked element is already suited for production usage
	 */
	boolean usable();
}
