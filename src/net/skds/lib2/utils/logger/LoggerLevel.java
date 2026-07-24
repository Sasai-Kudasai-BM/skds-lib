package net.skds.lib2.utils.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.utils.AnsiEscape;

@AllArgsConstructor
public enum LoggerLevel {
	SYSTEM_OUT("SYS_OUT", AnsiEscape.GREEN.sequence, OutType.OUT),
	DEBUG("DEBUG", AnsiEscape.BRIGHT_BLACK.sequence, OutType.OUT),
	INFO("INFO", AnsiEscape.BRIGHT_MAGENTA.sequence, OutType.OUT),
	LOG("LOG", AnsiEscape.BLUE.sequence, OutType.OUT),
	WARN("WARN", AnsiEscape.YELLOW.sequence, OutType.ERR),
	SYSTEM_ERR("SYS_ERR", AnsiEscape.BRIGHT_RED.sequence, OutType.ERR),
	ERROR("ERROR", AnsiEscape.BRIGHT_RED.sequence, OutType.ERR);

	final String msg;
	@Getter
	private String color;

	final OutType outType;

	void setColor(AnsiEscape ansi) {
		this.color = ansi.sequence;
	}
}
