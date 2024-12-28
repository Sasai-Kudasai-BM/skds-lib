package net.skds.lib2.utils.logger;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class SKDSLogger {

	static final ConcurrentLinkedQueue<PrintStream> attachedPrintStreams = new ConcurrentLinkedQueue<>();
	protected static boolean useGlobalPrintStream = true;
	protected static boolean useFileOut = true;

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
		System.setOut(new CustomPrintStream(CustomPrintStream.Type.OUT, ORIGINAL_OUT));
		System.setErr(new CustomPrintStream(CustomPrintStream.Type.ERR, ORIGINAL_ERR));
	}


	static void printLn(LoggerLevel level) {
		if (!SKDSLoggerConfig.getLevels().contains(level)) return;
		long time = System.currentTimeMillis();
		LogPrintln e = new LogPrintln(time, level, useGlobalPrintStream, useFileOut);
		LogWriter.INSTANCE.add(e);
	}

}
