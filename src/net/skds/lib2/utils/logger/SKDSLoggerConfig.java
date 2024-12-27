package net.skds.lib2.utils.logger;

import lombok.AccessLevel;
import lombok.Getter;
import net.skds.lib2.utils.json.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.Locale;

public final class SKDSLoggerConfig {

	@Getter
	private static SKDSLoggerConfig instance;

	@Getter(AccessLevel.PACKAGE)
	private EnumSet<LoggerLevel> levels = EnumSet.of(LoggerLevel.INFO, LoggerLevel.LOG, LoggerLevel.WARN, LoggerLevel.ERROR);
	@Getter(AccessLevel.PACKAGE)
	private SimpleDateFormat timeFormat;
	@Getter(AccessLevel.PACKAGE)
	private SimpleDateFormat dateFormat;
	@Getter(AccessLevel.PACKAGE)
	private boolean logThread;
	@Getter(AccessLevel.PACKAGE)
	private boolean logStackTop;
	@Getter(AccessLevel.PACKAGE)
	private boolean includeLoggerClass;
	@Getter(AccessLevel.PACKAGE)
	private String logDir;

	private SKDSLoggerConfig(Cfg cfg) {
		this.dateFormat = new SimpleDateFormat(cfg.dateFormat, Locale.ENGLISH);
		this.timeFormat = new SimpleDateFormat(cfg.timeFormat, Locale.ENGLISH);
		this.logThread = cfg.includeThread;
		this.logStackTop = cfg.includeStackTop;
		this.includeLoggerClass = cfg.includeLoggerClass;
		this.logDir = cfg.logDir;
	}


	@SuppressWarnings("FieldMayBeFinal")
	private static final class Cfg {
		private String timeFormat = "HH:mm:ss.SSS";
		private String dateFormat = "yyyy-MM/dd";
		private String logDir = "logs";
		private boolean includeThread = true;
		private boolean includeLoggerClass = false;
		private boolean includeStackTop = true;
	}

	public static void load() {
		Cfg cfg = null;
		try (InputStream is = SKDSLoggerConfig.class.getClassLoader().getResourceAsStream("SKDSLog.json")) {
			if (is != null) {
				cfg = JsonUtils.readConfig(is, Cfg.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (cfg == null) cfg = new Cfg();
		instance = new SKDSLoggerConfig(cfg);
	}

	public static void setLevels(LoggerLevel level, LoggerLevel... levels) {
		instance.levels = EnumSet.of(level, levels);
	}

	@SuppressWarnings("ManualArrayToCollectionCopy")
	public static void setLevelsFromAbove(LoggerLevel level) {
		EnumSet<LoggerLevel> newLevels = EnumSet.of(level);
		LoggerLevel[] values = LoggerLevel.values();
		for (int i = level.ordinal() + 1; i < values.length; i++) {
			newLevels.add(values[i]);
		}
		instance.levels = newLevels;
	}

	static {
		load();
	}
}
