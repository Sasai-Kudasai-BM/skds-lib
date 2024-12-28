package net.skds.lib2.utils.logger;

import java.io.PrintStream;

record LogEntry(long time, String message, LoggerLevel level, String thread, StackTraceElement trace,
				Class<?> loggingClass,
				Iterable<PrintStream> attachedPrintStreams, boolean useGlobalPrintStream, boolean useFileOut) {

}
