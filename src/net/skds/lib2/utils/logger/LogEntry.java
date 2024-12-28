package net.skds.lib2.utils.logger;

import java.io.PrintStream;

record LogEntry(String message, long time, LoggerLevel level, String thread, StackTraceElement trace,
				Class<?> loggingClass, boolean useGlobalPrintStream, Iterable<PrintStream> attachedPrintStreams) {

}
