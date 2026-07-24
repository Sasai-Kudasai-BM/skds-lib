package net.skds.lib2.utils.logger;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.function.Supplier;

public class SKDSLogger {

	static final SKDSLogger GLOBAL_LOGGER = new SKDSLogger(SKDSLogger.class);

	public static final PrintStream ORIGINAL_OUT = System.out;
	public static final PrintStream ORIGINAL_ERR = System.err;
	public static final PrintStream REPLACED_OUT = new CustomPrintStream(LoggerLevel.SYSTEM_OUT, ORIGINAL_OUT, GLOBAL_LOGGER);
	public static final PrintStream REPLACED_ERR = new CustomPrintStream(LoggerLevel.SYSTEM_ERR, ORIGINAL_ERR, GLOBAL_LOGGER);

	public static final PrintStream DEBUG_PRINTSTREAM = new CustomPrintStream(LoggerLevel.DEBUG, ORIGINAL_OUT, GLOBAL_LOGGER);
	public static final PrintStream INFO_PRINTSTREAM = new CustomPrintStream(LoggerLevel.INFO, ORIGINAL_OUT, GLOBAL_LOGGER);
	public static final PrintStream LOG_PRINTSTREAM = new CustomPrintStream(LoggerLevel.LOG, ORIGINAL_OUT, GLOBAL_LOGGER);
	public static final PrintStream WARN_PRINTSTREAM = new CustomPrintStream(LoggerLevel.WARN, ORIGINAL_OUT, GLOBAL_LOGGER);
	public static final PrintStream ERROR_PRINTSTREAM = new CustomPrintStream(LoggerLevel.ERROR, ORIGINAL_OUT, GLOBAL_LOGGER);

	private static final int DEPTH = 3;
	static final PrintStream[] PRINT_STREAM_ARRAY = {};

	private final String name;
	private final Supplier<SKDSLoggerConfig> configGetter;

	protected LinkedList<PrintStream> attachedPrintStreams = new LinkedList<>();
	protected PrintStream[] attachedPrintStreamsArray = {};
	private boolean useGlobalPrintStream = true;
	private Boolean useFileOut = null;

	public SKDSLogger() {
		Class<?> c;
		try {
			c = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		this.name = c.getSimpleName();
		this.configGetter = SKDSLoggerConfig::getInstance;
	}

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
		LogWriter.LogWriteable e;
		if (trace) {
			String thread = null;
			StackTraceElement stackTop = null;
			String loggingClass = null;
			if (config.isLogThread()) {
				thread = Thread.currentThread().getName();
			}
			if (config.isIncludeLoggerClass()) {
				loggingClass = this.name;
			}
			if (config.isLogStackTop()) {
				stackTop = Thread.currentThread().getStackTrace()[depth];
			}
			e = new LogLnEntry(time, message, level, thread, stackTop, loggingClass, attachedPrintStreamsArray, useGlobalPrintStream, isAttachToFile(), ln);
		} else {
			e = new LogEntry(time, message, level, attachedPrintStreamsArray, useGlobalPrintStream, isAttachToFile(), ln);
		}
		LogWriter.INSTANCE.add(e);
	}

	protected void update0(LoggerLevel level, int chars, String msg) {
		final SKDSLoggerConfig config = configGetter.get();
		if (!config.getLevels().contains(level)) return;

		if (chars != 0) {
			LogWriter.INSTANCE.add(new ResetEntry(chars, level, attachedPrintStreamsArray, useGlobalPrintStream));
		}
		if (msg != null) {
			long time = System.currentTimeMillis();
			LogWriter.INSTANCE.add(new LogEntry(time, msg, level, attachedPrintStreamsArray, useGlobalPrintStream, false, false));
		}
	}

	public void sout(Object msg) {
		log0(LoggerLevel.SYSTEM_OUT, DEPTH, true, true, msg);
	}

	public void serr(Object msg) {
		log0(LoggerLevel.SYSTEM_ERR, DEPTH, true, true, msg);
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

	public void soutNoBreak(Object msg) {
		log0(LoggerLevel.SYSTEM_OUT, DEPTH, false, true, msg);
	}

	public void serrNoBreak(Object msg) {
		log0(LoggerLevel.SYSTEM_ERR, DEPTH, false, true, msg);
	}

	public void debugNoBreak(Object msg) {
		log0(LoggerLevel.DEBUG, DEPTH, false, true, msg);
	}

	public void infoNoBreak(Object msg) {
		log0(LoggerLevel.INFO, DEPTH, false, true, msg);
	}

	public void logNoBreak(Object msg) {
		log0(LoggerLevel.LOG, DEPTH, false, true, msg);
	}

	public void warnNoBreak(Object msg) {
		log0(LoggerLevel.WARN, DEPTH, false, true, msg);
	}

	public void errorNoBreak(Object msg) {
		log0(LoggerLevel.ERROR, DEPTH, false, true, msg);
	}

	public void outFinish(Object msg) {
		log0(LoggerLevel.SYSTEM_OUT, DEPTH, true, false, msg);
	}

	public void errFinish(Object msg) {
		log0(LoggerLevel.SYSTEM_ERR, DEPTH, true, false, msg);
	}

	public void outUpdate(int backChars, Object msg) {
		update0(LoggerLevel.SYSTEM_OUT, backChars, String.valueOf(msg));
	}

	public void errUpdate(int backChars, Object msg) {
		update0(LoggerLevel.SYSTEM_ERR, backChars, String.valueOf(msg));
	}

	public void outClear() {
		update0(LoggerLevel.SYSTEM_OUT, -1, null);
	}

	public void errClear() {
		update0(LoggerLevel.SYSTEM_ERR, -1, null);
	}

	@Deprecated(forRemoval = true)
	public void soutNoWrap(Object msg) {
		log0(LoggerLevel.SYSTEM_OUT, DEPTH, false, true, msg);
	}

	@Deprecated(forRemoval = true)
	public void serrNoWrap(Object msg) {
		log0(LoggerLevel.SYSTEM_ERR, DEPTH, false, true, msg);
	}

	@Deprecated(forRemoval = true)
	public void debugNoWrap(Object msg) {
		log0(LoggerLevel.DEBUG, DEPTH, false, true, msg);
	}

	@Deprecated(forRemoval = true)
	public void infoNoWrap(Object msg) {
		log0(LoggerLevel.INFO, DEPTH, false, true, msg);
	}

	@Deprecated(forRemoval = true)
	public void logNoWrap(Object msg) {
		log0(LoggerLevel.LOG, DEPTH, false, true, msg);
	}

	@Deprecated(forRemoval = true)
	public void warnNoWrap(Object msg) {
		log0(LoggerLevel.WARN, DEPTH, false, true, msg);
	}

	@Deprecated(forRemoval = true)
	public void errorNoWrap(Object msg) {
		log0(LoggerLevel.ERROR, DEPTH, false, true, msg);
	}

	@Deprecated(forRemoval = true)
	public void soutContinue(Object msg) {
		log0(LoggerLevel.SYSTEM_OUT, DEPTH, false, false, msg);
	}

	@Deprecated(forRemoval = true)
	public void serrContinue(Object msg) {
		log0(LoggerLevel.SYSTEM_ERR, DEPTH, false, false, msg);
	}

	@Deprecated(forRemoval = true)
	public void debugContinue(Object msg) {
		log0(LoggerLevel.DEBUG, DEPTH, false, false, msg);
	}

	@Deprecated(forRemoval = true)
	public void infoContinue(Object msg) {
		log0(LoggerLevel.INFO, DEPTH, false, false, msg);
	}

	@Deprecated(forRemoval = true)
	public void logContinue(Object msg) {
		log0(LoggerLevel.LOG, DEPTH, false, false, msg);
	}

	@Deprecated(forRemoval = true)
	public void warnContinue(Object msg) {
		log0(LoggerLevel.WARN, DEPTH, false, false, msg);
	}

	@Deprecated(forRemoval = true)
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

	public boolean isAttachToGlobal() {
		return this.useGlobalPrintStream;
	}

	public void setAttachToFile(boolean attached) {
		useFileOut = attached;
	}

	public boolean isAttachToFile() {
		if (this.useFileOut == null) {
			SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
			if (config == null) return false;
			this.useFileOut = config.isUseFileOut();
		}
		return this.useFileOut;
	}

	public static void replaceOuts() {
		SKDSLoggerConfig.init();
		System.setOut(REPLACED_OUT);
		System.setErr(REPLACED_ERR);
	}

	void printLn(LoggerLevel level) {
		if (!configGetter.get().getLevels().contains(level)) return;
		long time = System.currentTimeMillis();
		LogPrintln e = new LogPrintln(time, level, attachedPrintStreamsArray, useGlobalPrintStream, isAttachToFile());
		LogWriter.INSTANCE.add(e);
	}
}
