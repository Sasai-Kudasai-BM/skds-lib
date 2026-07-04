package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.AnsiEscape;

import java.io.PrintStream;

record ResetEntry(int chars, LoggerLevel level, PrintStream[] attachedStreams,
				  boolean useGlobalPrintStream) implements LogWriter.LogWriteable {

	@Override
	public void write() {
		LogWriter.write(null, chars == -1 ? AnsiEscape.clearLine() : AnsiEscape.clearBack(chars), level, attachedStreams, useGlobalPrintStream, null);
	}

	@Override
	public EntryType entryType() {
		return EntryType.RESET;
	}

	@Override
	public OutType outType() {
		return level.outType;
	}
}
