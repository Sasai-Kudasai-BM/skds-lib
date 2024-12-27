package net.skds.lib2.utils.logger;

record LogEntry(String message, long time, LoggerLevel level, String thread, StackTraceElement trace,
				Class<?> loggingClass) {
	
}
