package net.skds.lib2.utils.logger;


final class SKDSLoggerImpl extends SKDSLogger {

	private final Class<?> loggingClass;

	public SKDSLoggerImpl(Class<?> loggingClass) {
		this.loggingClass = loggingClass;
	}

	@Override
	public void log0(LoggerLevel level, Object msg) {
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
			stackTop = trace[3];
		}

		LogEntry e = new LogEntry(message, time, level, thread, stackTop, loggingClass);

		LogWriter.INSTANCE.add(e);

	}

	private boolean isLoggingLevel(LoggerLevel level) {
		return SKDSLoggerConfig.getInstance().getLevels().contains(level);
	}

}
