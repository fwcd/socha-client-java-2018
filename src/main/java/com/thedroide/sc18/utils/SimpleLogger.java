package com.thedroide.sc18.utils;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * A simple (though not performance-optimized) logger implementation.
 */
public class SimpleLogger implements Logger {
	private final Consumer<String> out;
	private Level level = Level.INFO;

	public static enum Level {
		TRACE(-2), DEBUG(-1), INFO(0), WARN(1), ERROR(2);

		private final int i;

		private Level(int i) {
			this.i = i;
		}

		public int index() {
			return i;
		}
	}

	public SimpleLogger(Consumer<String> out) {
		this.out = out;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
	private void println(String ln) {
		out.accept(ln + "\n");
	}

	private void println(String prefix, String format, Object... args) {
		StringBuilder result = new StringBuilder().append(prefix).append(' ');

		int i = 0;
		for (char c : format.toCharArray()) {
			if (c == '{') {
				result.append(args[i++]);
			} else if (c != '}') {
				result.append(c);
			}
		}

		println(result.toString());
	}

	@Override
	public void debug(String arg0) {
		println("DEBUG", arg0);
	}

	@Override
	public void debug(String arg0, Object arg1) {
		println("DEBUG", arg0, arg1);
	}

	@Override
	public void debug(String arg0, Object[] arg1) {
		println("DEBUG", arg0, arg1);
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
		println("DEBUG", arg0, arg1);
	}

	@Override
	public void debug(Marker arg0, String arg1) {
		println("DEBUG", arg1);
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2) {
		println("DEBUG", arg0, arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2) {
		println("DEBUG", arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object[] arg2) {
		println("DEBUG", arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Throwable arg2) {
		println("DEBUG", arg1, arg2);
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
		println("DEBUG", arg1, arg2, arg3);
	}

	@Override
	public void error(String arg0) {
		println("ERROR", arg0);
	}

	@Override
	public void error(String arg0, Object arg1) {
		println("ERROR", arg0, arg1);
	}

	@Override
	public void error(String arg0, Object[] arg1) {
		println("ERROR", arg0, arg1);
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		println("ERROR", arg0, arg1);
	}

	@Override
	public void error(Marker arg0, String arg1) {
		println("ERROR", arg1);
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2) {
		println("ERROR", arg0, arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2) {
		println("ERROR", arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Object[] arg2) {
		println("ERROR", arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Throwable arg2) {
		println("ERROR", arg1, arg2);
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
		println("ERROR", arg1, arg2, arg3);
	}

	@Override
	public String getName() {
		return "SimpleLogger";
	}

	@Override
	public void info(String arg0) {
		println("INFO", arg0);
	}

	@Override
	public void info(String arg0, Object arg1) {
		println("INFO", arg0, arg1);
	}

	@Override
	public void info(String arg0, Object[] arg1) {
		println("INFO", arg0, arg1);
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		println("INFO", arg0, arg1);
	}

	@Override
	public void info(Marker arg0, String arg1) {
		println("INFO", arg1);
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2) {
		println("INFO", arg0, arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2) {
		println("INFO", arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Object[] arg2) {
		println("INFO", arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Throwable arg2) {
		println("INFO", arg1, arg2);
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
		println("INFO", arg1, arg2, arg3);
	}

	private boolean isEnabled(Level l) {
		return level.index() <= l.index();
	}

	@Override
	public boolean isDebugEnabled() {
		return isEnabled(Level.DEBUG);
	}

	@Override
	public boolean isDebugEnabled(Marker arg0) {
		return isEnabled(Level.DEBUG);
	}

	@Override
	public boolean isErrorEnabled() {
		return isEnabled(Level.ERROR);
	}

	@Override
	public boolean isErrorEnabled(Marker arg0) {
		return isEnabled(Level.ERROR);
	}

	@Override
	public boolean isInfoEnabled() {
		return isEnabled(Level.INFO);
	}

	@Override
	public boolean isInfoEnabled(Marker arg0) {
		return isEnabled(Level.INFO);
	}

	@Override
	public boolean isTraceEnabled() {
		return isEnabled(Level.TRACE);
	}

	@Override
	public boolean isTraceEnabled(Marker arg0) {
		return isEnabled(Level.TRACE);
	}

	@Override
	public boolean isWarnEnabled() {
		return isEnabled(Level.WARN);
	}

	@Override
	public boolean isWarnEnabled(Marker arg0) {
		return isEnabled(Level.WARN);
	}

	@Override
	public void trace(String arg0) {
		println("TRACE", arg0);
	}

	@Override
	public void trace(String arg0, Object arg1) {
		println("TRACE", arg0, arg1);
	}

	@Override
	public void trace(String arg0, Object[] arg1) {
		println("TRACE", arg0, arg1);
	}

	@Override
	public void trace(String arg0, Throwable arg1) {
		println("TRACE", arg0, arg1);
	}

	@Override
	public void trace(Marker arg0, String arg1) {
		println("TRACE", arg1);
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2) {
		println("TRACE", arg0, arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2) {
		println("TRACE", arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Object[] arg2) {
		println("TRACE", arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Throwable arg2) {
		println("TRACE", arg1, arg2);
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
		println("TRACE", arg1, arg2, arg3);
	}

	@Override
	public void warn(String arg0) {
		println("WARN", arg0);
	}

	@Override
	public void warn(String arg0, Object arg1) {
		println("WARN", arg0, arg1);
	}

	@Override
	public void warn(String arg0, Object[] arg1) {
		println("WARN", arg0, arg1);
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		println("WARN", arg0, arg1);
	}

	@Override
	public void warn(Marker arg0, String arg1) {
		println("WARN", arg1);
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2) {
		println("WARN", arg0, arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2) {
		println("WARN", arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Object[] arg2) {
		println("WARN", arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Throwable arg2) {
		println("WARN", arg1, arg2);
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
		println("WARN", arg1, arg2, arg3);
	}
}
