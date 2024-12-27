package net.skds.lib2.utils.logger;

record LogEntry(String message, long time, LoggerLevel level, String thread, StackTraceElement trace,
				Class<?> loggingClass) {

	void backLogInfo(String decoratedMsg) {
		SKDSLogger.ORIGINAL_OUT.print(decoratedMsg);
	}

	void backLogErr(String decoratedMsg) {
		SKDSLogger.ORIGINAL_ERR.print(decoratedMsg);
	}
}
