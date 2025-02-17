package net.skds.lib2.utils.logger;

import java.io.PrintStream;
import java.util.Date;

public record LogEntry(long time, String message, LoggerLevel level, PrintStream[] attachedStreams, boolean useGlobalPrintStream,
				  boolean useFileOut) implements LogWriter.LogWriteable {

	@Override
	public void write() {
		Date date = new Date(this.time);
		LogWriter.write(date, message, level, attachedStreams, useGlobalPrintStream, useFileOut ? message : null);
	}
	
}
