package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.logger.LogWriter.LogWriteable;

final class SKDSLoggerImpl extends SKDSLogger {

	private final Class<?> loggingClass;

	public SKDSLoggerImpl(Class<?> loggingClass) {
		this.loggingClass = loggingClass;
	}

	@Override
	protected void log0(LoggerLevel level, int depth, boolean ln, boolean trace, Object msg) {
		if (!isLoggingLevel(level)) return;
		String message = String.valueOf(msg);
		long time = System.currentTimeMillis();
		String thread = null;
		StackTraceElement stackTop = null;
		Class<?> loggingClass = null;
		if (trace) {
			SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
			if (config.isLogThread()) {
				thread = Thread.currentThread().getName();
			}
			if (config.isIncludeLoggerClass()) {
				loggingClass = this.loggingClass;
			}
			if (config.isLogStackTop()) {
				stackTop = Thread.currentThread().getStackTrace()[depth];
			}
		}
		LogWriteable e;
		if (ln) {
			e = new LogLnEntry(time, message, level, thread, stackTop, loggingClass, attachedPrintStreams.toArray(printStreamArray), useGlobalPrintStream, useFileOut, ln);
		} else {
			e = new LogEntry(time, message, level, attachedPrintStreams.toArray(printStreamArray), useGlobalPrintStream, useFileOut);
		}
		LogWriter.INSTANCE.add(e);
	}

	private boolean isLoggingLevel(LoggerLevel level) {
		return SKDSLoggerConfig.getLevels().contains(level);
	}

}
