package net.skds.lib2.utils.logger;

record LogEntry(long time, String message, LoggerLevel level, String thread, StackTraceElement trace,
				Class<?> loggingClass, boolean useGlobalPrintStream, boolean useFileOut) {

}
