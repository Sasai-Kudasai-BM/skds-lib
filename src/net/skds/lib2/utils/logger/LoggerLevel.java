package net.skds.lib2.utils.logger;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LoggerLevel {
	SYSTEM_OUT("OUT", AnsiEscape.createSequence(AnsiEscape.GREEN)),
	DEBUG("DEBUG", AnsiEscape.createSequence(AnsiEscape.CYAN)),
	INFO("INFO", AnsiEscape.createSequence(AnsiEscape.BRIGHT_MAGENTA)),
	LOG("LOG", AnsiEscape.createSequence(AnsiEscape.BLUE)),
	WARN("WARN", AnsiEscape.createSequence(AnsiEscape.YELLOW)),
	SYSTEM_ERR("SYSTEM_ERR", AnsiEscape.createSequence(AnsiEscape.BRIGHT_RED)),
	ERROR("ERROR", AnsiEscape.createSequence(AnsiEscape.BRIGHT_RED));

	final String msg;
	String color;

	void setColor(AnsiEscape name) {
		this.color = AnsiEscape.createSequence(name);
	}

	void setColor(AnsiEscape... names) {
		this.color = AnsiEscape.createSequence(names);
	}
}
