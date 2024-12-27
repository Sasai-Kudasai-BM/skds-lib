package net.skds.lib2.utils.logger;

import java.io.OutputStream;
import java.io.PrintStream;

class CustomPrintStream extends PrintStream {

	private static final SKDSLogger log = SKDSLoggerFactory.getLogger(CustomPrintStream.class);

	private final Type type;

	public CustomPrintStream(Type t, OutputStream out) {
		super(out);
		this.type = t;
	}

	private void logLine(String x) {
		switch (this.type) {
			case OUT -> log.info(x);
			case ERR -> log.err(x);
		}
	}

	public enum Type {
		ERR, OUT;
	}

	@Override
	public final void println(boolean x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(char x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(int x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(long x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(float x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(double x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void println(String x) {
		this.logLine(x);
	}

	@Override
	public final void println(Object x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void print(boolean x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void print(char x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void print(int x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void print(long x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void print(float x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void print(double x) {
		this.logLine(String.valueOf(x));
	}

	@Override
	public final void print(String x) {
		this.logLine(x);
	}

	@Override
	public final void print(Object x) {
		this.logLine(String.valueOf(x));
	}
	

}
