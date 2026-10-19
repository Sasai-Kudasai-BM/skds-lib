package net.skds.lib2.utils.logger;

import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.utils.AnsiEscape;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map.Entry;

public final class SKDSLoggerConfig {
	private static SKDSLoggerConfig instance;
	private final Cfg cfg;
	private final long logFileSplitSize;
	private final EnumSet<LoggerLevel> levels;
	private final SimpleDateFormat timeFormat;
	private final SimpleDateFormat dateFormat;
	private final boolean logThread;
	private final boolean useFileOut;
	private final boolean logStackTop;
	private final boolean includeLoggerClass;
	private final String logDir;

	public SKDSLoggerConfig(Cfg cfg) {
		this.cfg = cfg;
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

		public String getTimeFormat() {
			return this.timeFormat;
		}

		public String getDateFormat() {
			return this.dateFormat;
		}

		public String getLogDir() {
			return this.logDir;
		}

		public long getLogFileSplitSize() {
			return this.logFileSplitSize;
		}

		public boolean isIncludeThread() {
			return this.includeThread;
		}

		public boolean isIncludeLoggerClass() {
			return this.includeLoggerClass;
		}

		public boolean isIncludeStackTop() {
			return this.includeStackTop;
		}

		public boolean isUseFileOut() {
			return this.useFileOut;
		}

		public EnumMap<LoggerLevel, AnsiEscape> getAnsiColors() {
			return this.ansiColors;
		}

		public EnumSet<LoggerLevel> getLevels() {
			return this.levels;
		}

		public void setTimeFormat(final String timeFormat) {
			this.timeFormat = timeFormat;
		}

		public void setDateFormat(final String dateFormat) {
			this.dateFormat = dateFormat;
		}

		public void setLogDir(final String logDir) {
			this.logDir = logDir;
		}

		public void setLogFileSplitSize(final long logFileSplitSize) {
			this.logFileSplitSize = logFileSplitSize;
		}

		public void setIncludeThread(final boolean includeThread) {
			this.includeThread = includeThread;
		}

		public void setIncludeLoggerClass(final boolean includeLoggerClass) {
			this.includeLoggerClass = includeLoggerClass;
		}

		public void setIncludeStackTop(final boolean includeStackTop) {
			this.includeStackTop = includeStackTop;
		}

		public void setUseFileOut(final boolean useFileOut) {
			this.useFileOut = useFileOut;
		}

		public void setAnsiColors(final EnumMap<LoggerLevel, AnsiEscape> ansiColors) {
			this.ansiColors = ansiColors;
		}

		public void setLevels(final EnumSet<LoggerLevel> levels) {
			this.levels = levels;
		}
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
				cfg = SosisonUtils.readJson(is, Cfg.class);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (cfg == null) cfg = new Cfg();
		instance = new SKDSLoggerConfig(cfg);
	}

	public static SKDSLoggerConfig getInstance() {
		return SKDSLoggerConfig.instance;
	}

	public Cfg getCfg() {
		return this.cfg;
	}

	public long getLogFileSplitSize() {
		return this.logFileSplitSize;
	}

	public EnumSet<LoggerLevel> getLevels() {
		return this.levels;
	}

	public SimpleDateFormat getTimeFormat() {
		return this.timeFormat;
	}

	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
	}

	public boolean isLogThread() {
		return this.logThread;
	}

	public boolean isUseFileOut() {
		return this.useFileOut;
	}

	public boolean isLogStackTop() {
		return this.logStackTop;
	}

	public boolean isIncludeLoggerClass() {
		return this.includeLoggerClass;
	}

	public String getLogDir() {
		return this.logDir;
	}
}
