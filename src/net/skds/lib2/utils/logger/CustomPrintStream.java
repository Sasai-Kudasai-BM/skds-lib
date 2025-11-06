package net.skds.lib2.utils.logger;

import net.skds.lib2.io.CustomAbstractPrintStream;

import java.io.OutputStream;

class CustomPrintStream extends CustomAbstractPrintStream {

	protected final LoggerLevel type;
	protected final SKDSLogger logger;

	public CustomPrintStream(LoggerLevel t, OutputStream out, SKDSLogger logger) {
		super(out);
		this.type = t;
		this.logger = logger;
	}

	@Override
	protected void logLine(String x, boolean ln) {
		logger.log0(type, 4, ln, ln, x);
	}

	@Override
	public void println() {
		logger.printLn(type);
	}
}
