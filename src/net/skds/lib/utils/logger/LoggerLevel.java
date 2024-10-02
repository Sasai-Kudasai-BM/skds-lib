package net.skds.lib.utils.logger;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LoggerLevel {
	DEBUG("DEBUG"),
	INFO("INFO "),
	LOG("LOG  "),
	WARN("WARN "),
	ERROR("ERROR");

	final String msg;
}
