package net.skds.lib2.utils.logger;

import java.io.PrintStream;

public abstract class SKDSLogger {

	public static final PrintStream ORIGINAL_OUT = System.out;
	public static final PrintStream ORIGINAL_ERR = System.err;
	private static final int DEPTH = 3;

	protected abstract void log0(LoggerLevel level, int depth, Object msg);

	public void debug(Object msg) {
		log0(LoggerLevel.DEBUG, DEPTH, msg);
	}

	public void info(Object msg) {
		log0(LoggerLevel.INFO, DEPTH, msg);
	}

	public void log(Object msg) {
		log0(LoggerLevel.LOG, DEPTH, msg);
	}

	public void warn(Object msg) {
		log0(LoggerLevel.WARN, DEPTH, msg);
	}

	public void err(Object msg) {
		log0(LoggerLevel.ERROR, DEPTH, msg);
	}

	public abstract boolean attachPrintStream(PrintStream ps);

	public abstract boolean detachPrintStream(PrintStream ps);

	public abstract void setAttachToGlobal(boolean attached);

	public abstract void setAttachToFile(boolean attached);

	public static void replaceOuts() {
		System.setOut(new CustomPrintStream(CustomPrintStream.Type.OUT, ORIGINAL_OUT));
		System.setErr(new CustomPrintStream(CustomPrintStream.Type.ERR, ORIGINAL_ERR));
	}

}
