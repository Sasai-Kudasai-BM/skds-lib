package net.skds.lib2.utils.logger;

import net.skds.lib2.utils.AnsiEscape;

public enum LoggerLevel {
	SYSTEM_OUT("SYS_OUT", AnsiEscape.GREEN.sequence, OutType.OUT),
	DEBUG("DEBUG", AnsiEscape.BRIGHT_BLACK.sequence, OutType.OUT),
	INFO("INFO", AnsiEscape.BRIGHT_MAGENTA.sequence, OutType.OUT),
	LOG("LOG", AnsiEscape.BLUE.sequence, OutType.OUT),
	WARN("WARN", AnsiEscape.YELLOW.sequence, OutType.ERR),
	SYSTEM_ERR("SYS_ERR", AnsiEscape.BRIGHT_RED.sequence, OutType.ERR),
	ERROR("ERROR", AnsiEscape.BRIGHT_RED.sequence, OutType.ERR);

	final String msg;
	private String color;
	final OutType outType;

	void setColor(AnsiEscape ansi) {
		this.color = ansi.sequence;
	}

	private LoggerLevel(final String msg, final String color, final OutType outType) {
		this.msg = msg;
		this.color = color;
		this.outType = outType;
	}

	public String getColor() {
		return this.color;
	}
}
