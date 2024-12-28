package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.StringUtils;

import java.util.Date;

record LogEntry(long time, String message, LoggerLevel level, String thread, StackTraceElement trace,
				Class<?> loggingClass, boolean useGlobalPrintStream,
				boolean useFileOut) implements LogWriter.LogWriteable {

	@Override
	public void write() {
		Date date = new Date(time);
		SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
		StringBuilder logMsg = new StringBuilder(level.getColor());
		logMsg.append(config.getTimeFormat().format(date)).append(' ');
		if (loggingClass != null) {
			logMsg.append('[').append(loggingClass.getSimpleName()).append("] ");
		}
		if (thread != null) {
			logMsg.append('[').append(thread).append("] ");
		}
		StackTraceElement trace = trace();
		if (trace != null) {
			logMsg.append('[')
					.append(StringUtils.cutStringAfterFromEnd(trace.getClassName(), '.'))
					.append(':')
					.append(trace.getMethodName())
					.append('.')
					.append(trace.getLineNumber())
					.append("] ");
		}
		logMsg.append('[').append(level.msg).append("] ");
		logMsg.append(message).append('\n');
		String decoratedMsg = logMsg.toString();

		LogWriter.write(date, decoratedMsg, level, useGlobalPrintStream, useFileOut);
	}
}
