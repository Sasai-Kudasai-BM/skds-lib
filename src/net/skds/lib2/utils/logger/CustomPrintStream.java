package net.skds.lib2.utils.logger;

import java.io.OutputStream;

import net.skds.lib2.io.CustomAbstractPrintStream;

class CustomPrintStream extends CustomAbstractPrintStream {

	private static final SKDSLogger log = SKDSLoggerFactory.getLogger(CustomPrintStream.class);

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
			case OUT -> log.log0(LoggerLevel.SYSTEM_OUT, 4, ln, x);
			case ERR -> log.log0(LoggerLevel.SYSTEM_ERR, 4, ln, x);
		}
	}

	@Override
	public void println() {
		switch (this.type) {
			case OUT -> SKDSLogger.printLn(LoggerLevel.SYSTEM_OUT);
			case ERR -> SKDSLogger.printLn(LoggerLevel.SYSTEM_ERR);
		}
	}
}
