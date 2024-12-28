package net.skds.lib2.utils.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum LoggerLevel {
	SYSTEM_OUT("OUT", AnsiEscape.GREEN.sequence),
	DEBUG("DEBUG", AnsiEscape.CYAN.sequence),
	INFO("INFO", AnsiEscape.BRIGHT_MAGENTA.sequence),
	LOG("LOG", AnsiEscape.BLUE.sequence),
	WARN("WARN", AnsiEscape.YELLOW.sequence),
	SYSTEM_ERR("SYSTEM_ERR", AnsiEscape.BRIGHT_RED.sequence),
	ERROR("ERROR", AnsiEscape.BRIGHT_RED.sequence);

	final String msg;
	@Getter
	private String color;

	void setColor(AnsiEscape ansi) {
		this.color = ansi.sequence;
	}
}
