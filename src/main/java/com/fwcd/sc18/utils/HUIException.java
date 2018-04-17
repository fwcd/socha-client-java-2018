package com.fwcd.sc18.utils;

public class HUIException extends RuntimeException {
	private static final long serialVersionUID = 5022671247181443152L;
	
	public HUIException(String message) {
		super(message);
	}
	
	public HUIException(Throwable cause) {
		super(cause);
	}
}
