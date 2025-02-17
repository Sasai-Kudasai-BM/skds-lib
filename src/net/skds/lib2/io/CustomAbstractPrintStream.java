package net.skds.lib2.io;

import java.io.OutputStream;
import java.io.PrintStream;

public abstract class CustomAbstractPrintStream extends PrintStream {

	public CustomAbstractPrintStream(OutputStream out) {
		super(out);
	}

	protected abstract void logLine(String x, boolean ln);

	@Override
	public void println() {
		this.logLine("\n", false);
	}

	@Override
	public final void println(boolean x) {
		this.logLine(String.valueOf(x), true);
	}

	@Override
	public final void println(char x) {
		this.logLine(String.valueOf(x), true);
	}

	@Override
	public final void println(int x) {
		this.logLine(String.valueOf(x), true);
	}

	@Override
	public final void println(long x) {
		this.logLine(String.valueOf(x), true);
	}

	@Override
	public final void println(float x) {
		this.logLine(String.valueOf(x), true);
	}

	@Override
	public final void println(double x) {
		this.logLine(String.valueOf(x), true);
	}

	@Override
	public final void println(String x) {
		this.logLine(x, true);
	}

	@Override
	public final void println(Object x) {
		this.logLine(String.valueOf(x), true);
	}

	@Override
	public final void print(boolean x) {
		this.logLine(String.valueOf(x), false);
	}

	@Override
	public final void print(char x) {
		this.logLine(String.valueOf(x), false);
	}

	@Override
	public final void print(int x) {
		this.logLine(String.valueOf(x), false);
	}

	@Override
	public final void print(long x) {
		this.logLine(String.valueOf(x), false);
	}

	@Override
	public final void print(float x) {
		this.logLine(String.valueOf(x), false);
	}

	@Override
	public final void print(double x) {
		this.logLine(String.valueOf(x), false);
	}

	@Override
	public final void print(String x) {
		this.logLine(x, false);
	}

	@Override
	public final void print(Object x) {
		this.logLine(String.valueOf(x), false);
	}
}
