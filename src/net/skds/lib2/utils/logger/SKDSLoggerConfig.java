package net.skds.lib2.utils.logger;

import lombok.AccessLevel;
import lombok.Getter;
import net.skds.lib2.utils.json.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map.Entry;

public final class SKDSLoggerConfig {

	@Getter
	private static SKDSLoggerConfig instance;
	@Getter(AccessLevel.PACKAGE)
	private static EnumSet<LoggerLevel> levels = EnumSet.allOf(LoggerLevel.class);

	@Getter(AccessLevel.PACKAGE)
	private final SimpleDateFormat timeFormat;
	@Getter(AccessLevel.PACKAGE)
	private final SimpleDateFormat dateFormat;
	@Getter(AccessLevel.PACKAGE)
	private final boolean logThread;
	@Getter(AccessLevel.PACKAGE)
	private final boolean logStackTop;
	@Getter(AccessLevel.PACKAGE)
	private final boolean includeLoggerClass;
	@Getter(AccessLevel.PACKAGE)
	private final String logDir;

	private SKDSLoggerConfig(Cfg cfg) {
		this.dateFormat = new SimpleDateFormat(cfg.dateFormat, Locale.ENGLISH);
		this.timeFormat = new SimpleDateFormat(cfg.timeFormat, Locale.ENGLISH);
		this.logThread = cfg.includeThread;
		this.logStackTop = cfg.includeStackTop;
		this.includeLoggerClass = cfg.includeLoggerClass;
		this.logDir = cfg.logDir;
		for (Entry<LoggerLevel, AnsiEscape> entry : cfg.ansiColors.entrySet()) {
			entry.getKey().setColor(entry.getValue());
		}
	}

	@SuppressWarnings("FieldMayBeFinal")
	private static final class Cfg {
		private String timeFormat = "HH:mm:ss.SSS";
		private String dateFormat = "yyyy-MM/dd";
		private String logDir = "logs";
		private boolean includeThread = true;
		private boolean includeLoggerClass = false;
		private boolean includeStackTop = true;
		private EnumMap<LoggerLevel, AnsiEscape> ansiColors = new EnumMap<>(LoggerLevel.class);
	}

	private static void load() {
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
		SKDSLoggerConfig.levels = EnumSet.of(level, levels);
	}

	@SuppressWarnings("ManualArrayToCollectionCopy")
	public static void setLevelsFromAbove(LoggerLevel level) {
		EnumSet<LoggerLevel> newLevels = EnumSet.of(level);
		LoggerLevel[] values = LoggerLevel.values();
		for (int i = level.ordinal() + 1; i < values.length; i++) {
			newLevels.add(values[i]);
		}
		levels = newLevels;
	}

	static {
		load();
	}
}
