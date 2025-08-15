package net.skds.lib2.utils.logger;

import lombok.Getter;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.function.Supplier;

public class SKDSLogger {

	public static final PrintStream ORIGINAL_OUT = System.out;
	public static final PrintStream ORIGINAL_ERR = System.err;
	public static final PrintStream REPLACED_OUT = new CustomPrintStream(CustomPrintStream.Type.OUT, ORIGINAL_OUT);
	public static final PrintStream REPLACED_ERR = new CustomPrintStream(CustomPrintStream.Type.ERR, ORIGINAL_ERR);
	private static final int DEPTH = 3;
	static final PrintStream[] PRINT_STREAM_ARRAY = {};

	private final String name;
	private final Supplier<SKDSLoggerConfig> configGetter;

	protected LinkedList<PrintStream> attachedPrintStreams = new LinkedList<>();
	protected PrintStream[] attachedPrintStreamsArray = {};
	protected boolean useGlobalPrintStream = true;
	protected boolean useFileOut = true;

	public SKDSLogger(Class<?> loggingClass) {
		this.name = loggingClass.getSimpleName();
		this.configGetter = SKDSLoggerConfig::getInstance;
	}

	public SKDSLogger(String name) {
		this.name = name;
		this.configGetter = SKDSLoggerConfig::getInstance;
	}

	public SKDSLogger(String name, SKDSLoggerConfig.Cfg config) {
		this.name = name;
		SKDSLoggerConfig config1 = new SKDSLoggerConfig(config);
		this.configGetter = () -> config1;
	}

	protected void log0(LoggerLevel level, int depth, boolean ln, boolean trace, Object msg) {
		final SKDSLoggerConfig config = configGetter.get();
		if (!config.getLevels().contains(level)) return;
		String message = String.valueOf(msg);
		long time = System.currentTimeMillis();
		String thread = null;
		StackTraceElement stackTop = null;
		String loggingClass = null;
		if (trace) {
			if (config.isLogThread()) {
				thread = Thread.currentThread().getName();
			}
			if (config.isIncludeLoggerClass()) {
				loggingClass = this.name;
			}
			if (config.isLogStackTop()) {
				stackTop = Thread.currentThread().getStackTrace()[depth];
			}
		}
		LogWriter.LogWriteable e;
		if (ln) {
			e = new LogLnEntry(time, message, level, thread, stackTop, loggingClass, attachedPrintStreamsArray, useGlobalPrintStream, useFileOut, ln);
		} else {
			e = new LogEntry(time, message, level, attachedPrintStreamsArray, useGlobalPrintStream, useFileOut);
		}
		LogWriter.INSTANCE.add(e);
	}

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

	public synchronized boolean attachPrintStream(PrintStream ps) {
		attachedPrintStreams.add(ps);
		this.attachedPrintStreamsArray = attachedPrintStreams.toArray(PRINT_STREAM_ARRAY);
		return true;
	}

	public synchronized boolean detachPrintStream(PrintStream ps) {
		boolean b = attachedPrintStreams.remove(ps);
		if (b) this.attachedPrintStreamsArray = attachedPrintStreams.toArray(PRINT_STREAM_ARRAY);
		return b;
	}

	public void setAttachToGlobal(boolean attached) {
		useGlobalPrintStream = attached;
	}

	public void setAttachToFile(boolean attached) {
		useFileOut = attached;
	}

	public static void replaceOuts() {
		SKDSLoggerConfig.init();
		System.setOut(REPLACED_OUT);
		System.setErr(REPLACED_ERR);
	}

	void printLn(LoggerLevel level) {
		if (!configGetter.get().getLevels().contains(level)) return;
		long time = System.currentTimeMillis();
		LogPrintln e = new LogPrintln(time, level, attachedPrintStreamsArray, useGlobalPrintStream, useFileOut);
		LogWriter.INSTANCE.add(e);
	}
}
