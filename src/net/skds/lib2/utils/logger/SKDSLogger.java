package net.skds.lib2.utils.logger;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class SKDSLogger {

	static final ConcurrentLinkedQueue<PrintStream> attachedPrintStreams = new ConcurrentLinkedQueue<>();
	protected static boolean useGlobalPrintStream = true;
	protected static boolean useFileOut = true;

	public static final PrintStream ORIGINAL_OUT = System.out;
	public static final PrintStream ORIGINAL_ERR = System.err;
	public static final PrintStream REPLACED_OUT = new CustomPrintStream(CustomPrintStream.Type.OUT, ORIGINAL_OUT);
	public static final PrintStream REPLACED_ERR = new CustomPrintStream(CustomPrintStream.Type.ERR, ORIGINAL_ERR);
	private static final int DEPTH = 3;
	static final PrintStream[] printStreamArray = {};

	protected abstract void log0(LoggerLevel level, int depth, boolean ln, boolean trace, Object msg);

	public void debug(Object msg) {
		log0(LoggerLevel.DEBUG, DEPTH, true, true, msg);
	}

	public void info(Object msg) {
		log0(LoggerLevel.INFO, DEPTH, true, true, msg);
	}

	public void log(Object msg) {
		log0(LoggerLevel.LOG, DEPTH, true, true, msg);
	}

	public void warn(Object msg) {
		log0(LoggerLevel.WARN, DEPTH, true, true, msg);
	}

	public void error(Object msg) {
		log0(LoggerLevel.ERROR, DEPTH, true, true, msg);
	}

	public void debugNoWrap(Object msg) {
		log0(LoggerLevel.DEBUG, DEPTH, false, true, msg);
	}

	public void infoNoWrap(Object msg) {
		log0(LoggerLevel.INFO, DEPTH, false, true, msg);
	}

	public void logNoWrap(Object msg) {
		log0(LoggerLevel.LOG, DEPTH, false, true, msg);
	}

	public void warnNoWrap(Object msg) {
		log0(LoggerLevel.WARN, DEPTH, false, true, msg);
	}

	public void errorNoWrap(Object msg) {
		log0(LoggerLevel.ERROR, DEPTH, false, true, msg);
	}

	public void debugContinue(Object msg) {
		log0(LoggerLevel.DEBUG, DEPTH, false, false, msg);
	}

	public void infoContinue(Object msg) {
		log0(LoggerLevel.INFO, DEPTH, false, false, msg);
	}

	public void logContinue(Object msg) {
		log0(LoggerLevel.LOG, DEPTH, false, false, msg);
	}

	public void warnContinue(Object msg) {
		log0(LoggerLevel.WARN, DEPTH, false, false, msg);
	}

	public void errorContinue(Object msg) {
		log0(LoggerLevel.ERROR, DEPTH, false, false, msg);
	}

	public static boolean attachPrintStream(PrintStream ps) {
		return attachedPrintStreams.offer(ps);
	}

	public static boolean detachPrintStream(PrintStream ps) {
		return attachedPrintStreams.remove(ps);
	}

	public static void setAttachToGlobal(boolean attached) {
		useGlobalPrintStream = attached;
	}

	public static void setAttachToFile(boolean attached) {
		useFileOut = attached;
	}

	public static void replaceOuts() {
		SKDSLoggerConfig.init();
		System.setOut(REPLACED_OUT);
		System.setErr(REPLACED_ERR);
	}

	static void printLn(LoggerLevel level) {
		if (!SKDSLoggerConfig.getLevels().contains(level)) return;
		long time = System.currentTimeMillis();
		LogPrintln e = new LogPrintln(time, level, attachedPrintStreams.toArray(printStreamArray), useGlobalPrintStream, useFileOut);
		LogWriter.INSTANCE.add(e);
	}
}
