package net.skds.lib2.utils.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.utils.AnsiEscape;

@AllArgsConstructor
public enum LoggerLevel {
	SYSTEM_OUT("SYS_OUT", AnsiEscape.GREEN.sequence),
	DEBUG("DEBUG", AnsiEscape.BRIGHT_BLACK.sequence),
	INFO("INFO", AnsiEscape.BRIGHT_MAGENTA.sequence),
	LOG("LOG", AnsiEscape.BLUE.sequence),
	WARN("WARN", AnsiEscape.YELLOW.sequence),
	SYSTEM_ERR("SYS_ERR", AnsiEscape.BRIGHT_RED.sequence),
	ERROR("ERROR", AnsiEscape.BRIGHT_RED.sequence);

	final String msg;
	@Getter
	private String color;

	void setColor(AnsiEscape ansi) {
		this.color = ansi.sequence;
	}
}
