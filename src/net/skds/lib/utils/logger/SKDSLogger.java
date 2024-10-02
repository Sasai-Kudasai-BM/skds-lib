package net.skds.lib.utils.logger;

public interface SKDSLogger {

	void log(LoggerLevel level, String msg);

	void log(LoggerLevel level, Object msg);

	default void debug(String msg) {
		log(LoggerLevel.DEBUG, msg);
	}

	default void debug(Object msg) {
		log(LoggerLevel.DEBUG, msg);
	}

	default void info(String msg) {
		log(LoggerLevel.INFO, msg);
	}

	default void info(Object msg) {
		log(LoggerLevel.INFO, msg);
	}

	default void log(String msg) {
		log(LoggerLevel.LOG, msg);
	}

	default void log(Object msg) {
		log(LoggerLevel.LOG, msg);
	}

	default void warn(String msg) {
		log(LoggerLevel.WARN, msg);
	}

	default void warn(Object msg) {
		log(LoggerLevel.WARN, msg);
	}

	default void err(String msg) {
		log(LoggerLevel.ERROR, msg);
	}

	default void err(Object msg) {
		log(LoggerLevel.ERROR, msg);
	}

}
