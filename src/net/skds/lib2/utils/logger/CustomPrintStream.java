package net.skds.lib2.utils.logger;

import net.skds.lib2.io.CustomAbstractPrintStream;

import java.io.OutputStream;

class CustomPrintStream extends CustomAbstractPrintStream {

	protected final Type type;

	public CustomPrintStream(Type t, OutputStream out) {
		super(out);
		this.type = t;
	}

	public enum Type {
		ERR, OUT;
	}

	@Override
	protected void logLine(String x, boolean ln) {
		switch (this.type) {
			case OUT -> SKDSLogger.GLOBAL_LOGGER.log0(LoggerLevel.SYSTEM_OUT, 4, ln, ln, x);
			case ERR -> SKDSLogger.GLOBAL_LOGGER.log0(LoggerLevel.SYSTEM_ERR, 4, ln, ln, x);
		}
	}

	@Override
	public void println() {
		switch (this.type) {
			case OUT -> SKDSLogger.GLOBAL_LOGGER.printLn(LoggerLevel.SYSTEM_OUT);
			case ERR -> SKDSLogger.GLOBAL_LOGGER.printLn(LoggerLevel.SYSTEM_ERR);
		}
	}
}
