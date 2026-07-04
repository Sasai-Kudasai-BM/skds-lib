package net.skds.lib2.utils.logger;

import java.io.PrintStream;
import java.util.Date;

record LogPrintln(long time, LoggerLevel level, PrintStream[] attachedStreams, boolean useGlobalPrintStream,
				  boolean useFileOut) implements LogWriter.LogWriteable {

	@Override
	public void write() {
		Date date = new Date(this.time);
		String decoratedMsg = "\n";
		LogWriter.write(date, decoratedMsg, level, attachedStreams, useGlobalPrintStream, useFileOut ? decoratedMsg : null);
	}

	@Override
	public EntryType entryType() {
		return EntryType.LINE;
	}

	@Override
	public OutType outType() {
		return level.outType;
	}
}
