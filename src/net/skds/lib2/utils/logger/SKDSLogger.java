package net.skds.lib2.utils.logger;

import java.io.PrintStream;

public abstract class SKDSLogger {

	public static final PrintStream ORIGINAL_OUT = System.out;
	public static final PrintStream ORIGINAL_ERR = System.err;

	protected abstract void log0(LoggerLevel level, Object msg);

	public void debug(Object msg) {
		log0(LoggerLevel.DEBUG, msg);
	}

	public void info(Object msg) {
		log0(LoggerLevel.INFO, msg);
	}

	public void log(Object msg) {
		log0(LoggerLevel.LOG, msg);
	}

	public void warn(Object msg) {
		log0(LoggerLevel.WARN, msg);
	}

	public void err(Object msg) {
		log0(LoggerLevel.ERROR, msg);
	}

	public static void replaceOuts() {
		System.setOut(new CustomPrintStream(CustomPrintStream.Type.OUT, ORIGINAL_OUT));
		System.setErr(new CustomPrintStream(CustomPrintStream.Type.ERR, ORIGINAL_ERR));
	}

}
