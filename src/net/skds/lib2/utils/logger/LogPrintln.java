package net.skds.lib2.utils.logger;

import java.io.PrintStream;
import java.util.Date;

record LogPrintln(long time, LoggerLevel level, PrintStream[] attachedStreams, boolean useGlobalPrintStream,
				  boolean useFileOut) implements LogWriter.LogWriteable {

	@Override
	public void write() {
		Date date = new Date(time);
		String decoratedMsg = "\n";
		LogWriter.write(date, decoratedMsg, level, attachedStreams, useGlobalPrintStream, useFileOut);
	}
}
