package net.skds.lib2.utils.logger;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.codec.CodecUtils;
import net.skds.lib2.utils.AnsiEscape;

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

	@Getter
	private final long logFileSplitSize;
	@Getter
	private final EnumSet<LoggerLevel> levels;
	@Getter()
	private final SimpleDateFormat timeFormat;
	@Getter()
	private final SimpleDateFormat dateFormat;
	@Getter()
	private final boolean logThread;
	@Getter()
	private final boolean useFileOut;
	@Getter()
	private final boolean logStackTop;
	@Getter()
	private final boolean includeLoggerClass;
	@Getter()
	private final String logDir;

	public SKDSLoggerConfig(Cfg cfg) {
		this.dateFormat = new SimpleDateFormat(cfg.dateFormat, Locale.ENGLISH);
		this.timeFormat = new SimpleDateFormat(cfg.timeFormat, Locale.ENGLISH);
		this.logThread = cfg.includeThread;
		this.logStackTop = cfg.includeStackTop;
		this.includeLoggerClass = cfg.includeLoggerClass;
		this.logDir = cfg.logDir;
		this.useFileOut = cfg.useFileOut;
		this.logFileSplitSize = cfg.logFileSplitSize;
		this.levels = cfg.levels;
		for (Entry<LoggerLevel, AnsiEscape> entry : cfg.ansiColors.entrySet()) {
			entry.getKey().setColor(entry.getValue());
		}
	}

	@Getter
	@Setter
	@SuppressWarnings("FieldMayBeFinal")
	public static final class Cfg {
		private String timeFormat = "HH:mm:ss.SSS";
		private String dateFormat = "yyyy-MM/dd";
		private String logDir = "logs";
		private long logFileSplitSize = 64 * 1024 * 1024; // 64Mb
		private boolean includeThread = true;
		private boolean includeLoggerClass = false;
		private boolean includeStackTop = true;
		private boolean useFileOut = false;
		private EnumMap<LoggerLevel, AnsiEscape> ansiColors = new EnumMap<>(LoggerLevel.class);
		private EnumSet<LoggerLevel> levels = EnumSet.allOf(LoggerLevel.class);
	}

	public static void set(Cfg cfg) {
		instance = new SKDSLoggerConfig(cfg);
	}

	public static void init() {
	}

	static {
		Cfg cfg = null;
		try (InputStream is = SKDSLoggerConfig.class.getClassLoader().getResourceAsStream("SKDSLog.json")) {
			if (is != null) {
				cfg = CodecUtils.readJson(is, Cfg.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (cfg == null) cfg = new Cfg();
		instance = new SKDSLoggerConfig(cfg);
	}
}
