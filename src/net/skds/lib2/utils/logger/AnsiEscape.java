package net.skds.lib2.utils.logger;

import java.util.Locale;
import java.util.stream.Stream;

public enum AnsiEscape {
	/**
     * Bright general attribute.
     */
    BRIGHT("1"),
	BLACK("30"),
	RED("31"),
	GREEN("32"),
	YELLOW("33"),
	BLUE("34"),
	MAGENTA("35"),
	CYAN("36"),
	WHITE("37"),
	DEFAULT("39"),
	BG_BLACK("40"),
	BG_RED("41"),
	BG_GREEN("42"),
	BG_YELLOW("43"),
	BG_BLUE("44"),
	BG_MAGENTA("45"),
	BG_CYAN("46"),
	BG_WHITE("47"),

	BRIGHT_BLACK("90"),
	BRIGHT_RED("91"),
	BRIGHT_GREEN("92"),
	BRIGHT_YELLOW("93"),
	BRIGHT_BLUE("94"),
	BRIGHT_MAGENTA("95"),
	BRIGHT_CYAN("96"),
	BRIGHT_WHITE("97")
	;

	private static final String DEFAULT_STYLE = "\u001b[m";

	private final String code;

	AnsiEscape(final String code) {
		this.code = code;
	}

	public static String getDefaultStyle() {
		return DEFAULT_STYLE;
	}

	public String getCode() {
		return code;
	}

	public static String createSequence(final String... names) {
		return createSequence(Stream.of(names).map(name -> Enum.valueOf(AnsiEscape.class, name.toUpperCase(Locale.ENGLISH))).toArray(AnsiEscape[]::new));
	}

	public static String createSequence(final AnsiEscape escape) {
		return "\u001b[" + escape.getCode() + "m";
	}

	public static String createSequence(final AnsiEscape... escapes) {
		if (escapes == null) {
			return getDefaultStyle();
		}
		final StringBuilder sb = new StringBuilder("\u001b[");
		boolean first = true;
		for (final AnsiEscape escape : escapes) {
			try {
				if (!first) {
					sb.append(";");
				}
				first = false;
				sb.append(escape.getCode());
			} catch (final Exception ex) {
				// Ignore the error.
			}
		}
		sb.append("m");
		return sb.toString();
	}
}

