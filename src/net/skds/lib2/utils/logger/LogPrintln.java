package net.skds.lib2.utils.logger;

import java.util.Date;

record LogPrintln(long time, LoggerLevel level, boolean useGlobalPrintStream,
				  boolean useFileOut) implements LogWriter.LogWriteable {

	@Override
	public void write() {
		Date date = new Date(time);
		String decoratedMsg = "\n";
		LogWriter.write(date, decoratedMsg, level, useGlobalPrintStream, useFileOut);
	}
}
