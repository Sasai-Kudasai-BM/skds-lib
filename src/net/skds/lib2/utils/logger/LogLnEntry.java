package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.AnsiEscape;
import net.skds.lib2.utils.StringUtils;

import java.io.PrintStream;
import java.util.Date;

record LogLnEntry(long time, String message, LoggerLevel level, String thread, StackTraceElement trace,
				  String loggingClass, PrintStream[] attachedStreams, boolean useGlobalPrintStream,
				  boolean useFileOut, boolean ln) implements LogWriter.LogWriteable {

	static final String TERMINATION = '\n' + AnsiEscape.NORMAL.sequence;
	//public static final int TERMINATION_LENGTH = TERMINATION.length() - 1;

	@Override
	public void write() {
		Date date = new Date(time);
		SKDSLoggerConfig config = SKDSLoggerConfig.getInstance();
		StringBuilder logMsg = new StringBuilder();
		logMsg.append(config.getTimeFormat().format(date)).append(' ');
		if (loggingClass != null) {
			logMsg.append('[').append(loggingClass).append("] ");
		}
		if (thread != null) {
			logMsg.append('[').append(thread).append("] ");
		}
		if (trace != null && !trace.getClassName().equals("java.lang.Throwable$WrappedPrintStream")) {
			logMsg.append('[')
					.append(StringUtils.cutStringAfterFromEnd(trace.getClassName(), '.'))
					.append(':')
					.append(trace.getMethodName())
					.append('.')
					.append(trace.getLineNumber())
					.append("] ");
		}
		logMsg.append('[').append(level.msg).append("] ");
		logMsg.append(this.message);
		String message = logMsg.toString();
		String decoratedMsg = level.getColor() + message + (ln ? TERMINATION : "");
		String fileOut = null;
		if (useFileOut) {
			fileOut = message;
			if (ln) {
				fileOut += "\n";
			}
		}

		LogWriter.write(date, decoratedMsg, level, attachedStreams, useGlobalPrintStream, fileOut);
	}

	@Override
	public EntryType entryType() {
		return EntryType.PRINT_LN;
	}

	@Override
	public OutType outType() {
		return level.outType;
	}
}
