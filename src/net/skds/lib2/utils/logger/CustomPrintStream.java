package net.skds.lib2.utils.logger;

import java.io.OutputStream;
import java.io.PrintStream;

public class CustomPrintStream extends PrintStream {

	private static final SKDSLogger log = SKDSLoggerFactory.getLogger(CustomPrintStream.class);

	protected final Type type;

	public CustomPrintStream(Type t, OutputStream out) {
		super(out);
		this.type = t;
	}

	protected void logLine(String x) {
		switch (this.type) {
			case OUT -> log.log0(LoggerLevel.SYSTEM_OUT, 4, x);
			case ERR -> log.log0(LoggerLevel.SYSTEM_ERR, 4, x);
		}
	}

	public enum Type {
		ERR, OUT;
	}

	@Override
	public void println() {
		switch (this.type) {
			case OUT -> SKDSLogger.printLn(LoggerLevel.SYSTEM_OUT);
			case ERR -> SKDSLogger.printLn(LoggerLevel.SYSTEM_ERR);
		}
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
