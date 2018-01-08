package com.thedroide.sc18.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * An (admittedly hacky) PrintWriter used to redirect the
 * output to a custom out.
 */
public class CustomPrintWriter extends PrintWriter {
	private final Consumer<String> printer;
	
	public CustomPrintWriter(Consumer<String> printer) {
		super(new ByteArrayOutputStream(0));
		this.printer = printer;
	}

	@Override
	public void write(char[] buf, int off, int len) {
		printer.accept(String.valueOf(buf));
	}

	@Override
	public void write(char[] buf) {
		printer.accept(String.valueOf(buf));
	}

	@Override
	public void write(String s, int off, int len) {
		printer.accept(s);
	}

	@Override
	public void write(String s) {
		printer.accept(s);
	}
}
