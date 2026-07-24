package net.skds.lib2.utils.logger;

import java.io.PrintStream;
import java.util.Date;

import static net.skds.lib2.utils.logger.LogLnEntry.TERMINATION;

record LogEntry(long time, String message,
				LoggerLevel level, PrintStream[] attachedStreams,
				boolean useGlobalPrintStream, boolean useFileOut, boolean ln
) implements LogWriter.LogWriteable {

	@Override
	public void write() {
		Date date = new Date(this.time);
		String msg = message;
		String fileMsg = null;
		if (useFileOut) {
			fileMsg = msg;
			if (ln) {
				fileMsg += "\n";
			}
		}
		if (ln) {
			msg += TERMINATION;
		}
		LogWriter.write(date, msg, level, attachedStreams, useGlobalPrintStream, fileMsg);
	}

	@Override
	public EntryType entryType() {
		return EntryType.PRINT;
	}

	@Override
	public OutType outType() {
		return level.outType;
	}
}
