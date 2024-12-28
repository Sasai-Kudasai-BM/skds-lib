package net.skds.lib2.utils.logger;


final class SKDSLoggerImpl extends SKDSLogger {

	private final Class<?> loggingClass;

	private boolean useGlobalPrintStream = true;
	private boolean useFileOut = true;

	public SKDSLoggerImpl(Class<?> loggingClass) {
		this.loggingClass = loggingClass;
	}

	@Override
	public void log0(LoggerLevel level, int depth, Object msg) {
		if (!isLoggingLevel(level)) return;
		String message = String.valueOf(msg);
		long time = System.currentTimeMillis();
		String thread = null;
		StackTraceElement stackTop = null;
		Class<?> loggingClass = null;

		SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();

		if (config.isLogThread()) {
			thread = Thread.currentThread().getName();
		}
		if (config.isIncludeLoggerClass()) {
			loggingClass = this.loggingClass;
		}
		if (config.isLogStackTop()) {
			var trace = Thread.currentThread().getStackTrace();
			stackTop = trace[depth];
		}

		LogEntry e = new LogEntry(time, message, level, thread, stackTop, loggingClass, useGlobalPrintStream, useFileOut);

		LogWriter.INSTANCE.add(e);

	}

	@Override
	public void setAttachToGlobal(boolean attached) {
		this.useGlobalPrintStream = attached;
	}

	@Override
	public void setAttachToFile(boolean attached) {
		this.useFileOut = attached;
	}

	private boolean isLoggingLevel(LoggerLevel level) {
		return SKDSLoggerConfig.getLevels().contains(level);
	}

}
