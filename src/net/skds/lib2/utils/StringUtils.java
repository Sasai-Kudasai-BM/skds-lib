package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;
import net.skds.lib2.io.chars.CharInput;
import net.skds.lib2.io.chars.CharOutput;
import net.skds.lib2.io.exception.EndOfInputException;
import net.skds.lib2.io.exception.EndOfOutputException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
@UtilityClass
public class StringUtils {

	public static final HexFormat HEX_FORMAT_LC = HexFormat.of();
	public static final HexFormat HEX_FORMAT_UC = HexFormat.of().withUpperCase();

	// TODO check calls
	public static String quote(String s) {
		return '"' + s.replace("\"", "\\\"") + '"';
	}

	public static String unquote(String s) {
		return s.substring(1, s.length() - 1).replace("\\\"", "\"");
	}

	public static String unicodeCharUC(int c) {
		return "\\u%04X".formatted(c);
	}

	public static String unicodeCharLC(int c) {
		return "\\u%04x".formatted(c);
	}

	public static String expFloatUC(double value) {
		return "%E".formatted(value);
	}

	public static String expFloatLC(double value) {
		return "%e".formatted(value);
	}

	public static String hexIntUC(long value) {
		return "0x%X".formatted(value);
	}

	public static String hexIntLC(long value) {
		return "0x%x".formatted(value);
	}

	public static List<String> split(String value, char delimiter) {
		List<String> list = new ArrayList<>();
		int prev = 0;
		int next;
		while ((next = value.indexOf(delimiter, prev)) != -1) {
			list.add(value.substring(prev, next));
			prev = next + 1;
		}
		list.add(value.substring(prev));
		return list;
	}

	public static String uppercaseUnderlined(String str) {
		int l = str.length();
		if (l <= 1) return str.toUpperCase();
		StringBuilder sb = new StringBuilder(l);
		char c0 = str.charAt(0);
		boolean upp = Character.isUpperCase(c0);
		sb.append(upp ? c0 : Character.toUpperCase(c0));
		for (int i = 1; i < l; i++) {
			char c = str.charAt(i);
			boolean upp2 = Character.isUpperCase(c);
			if (!upp && upp2) sb.append('_');
			sb.append(upp2 ? c : Character.toUpperCase(c));
			upp = upp2;
		}
		return sb.toString();
	}

	public static String uppercaseFirstChar(String str) {
		if (str.length() < 2) {
			if (str.isEmpty()) {
				return str;
			}
			return String.valueOf(Character.toUpperCase(str.charAt(0)));
		}
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String lowercaseFirstChar(String str) {
		if (str.length() < 2) {
			if (str.isEmpty()) {
				return str;
			}
			return String.valueOf(Character.toLowerCase(str.charAt(0)));
		}
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

	public static String cutStringBefore(String str, char split) {
		int i = str.indexOf(split);
		if (i == -1) {
			return str;
		}
		return str.substring(0, i);
	}

	public static String cutStringAfter(String str, char split) {
		int i = str.indexOf(split);
		if (i == -1) {
			return str;
		}
		if (i + 1 == str.length()) {
			return "";
		}
		return str.substring(i + 1);
	}

	public static String cutStringBeforeFromEnd(String str, char split) {
		int i = str.lastIndexOf(split);
		if (i == -1) {
			return str;
		}
		return str.substring(0, i);
	}

	public static String cutStringAfterFromEnd(String str, char split) {
		int i = str.lastIndexOf(split);
		if (i == -1) {
			return str;
		}
		if (i == str.length() - 1) {
			return str;
		}
		if (i + 1 == str.length()) {
			return "";
		}
		return str.substring(i + 1);
	}

	public static String formatNamed(String pattern, String startSequence, String endSequence, Function<String, Object> mappingFunction) {
		return new PatternFormatter(pattern, startSequence, endSequence).assemble(mappingFunction);
	}

	public static String formatNamed(String pattern, Function<String, Object> mappingFunction) {
		return new PatternFormatter(pattern, "${", "}").assemble(mappingFunction);
	}

	public static PatternFormatter createFormatter(String pattern) {
		return new PatternFormatter(pattern, "${", "}");
	}

	public static PatternFormatter createFormatter(String pattern, String startSequence, String endSequence) {
		return new PatternFormatter(pattern, startSequence, endSequence);
	}

	public static String formatSequence(String pattern, String delimiter, Object... values) {
		return new SequenceFormatter(pattern, delimiter).assemble(values);
	}

	public static String formatSequence(String pattern, Object... values) {
		return new SequenceFormatter(pattern, "%s").assemble(values);
	}

	public static SequenceFormatter createSequenceFormatter(String pattern, String delimiter) {
		return new SequenceFormatter(pattern, delimiter);
	}

	public static SequenceFormatter createSequenceFormatter(String pattern) {
		return new SequenceFormatter(pattern, "%s");
	}

	public static void writeQuoted(CharOutput output, String value, char quote) throws EndOfOutputException {
		output.append(quote);
		final int length = value.length();
		int escapeStack = 0;
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			if (c == quote) {
				if (escapeStack == 0) {
					output.append('\\');
				} else {
					for (int j = -1; j < escapeStack; j++) {
						output.append('\\');
					}
				}
				output.append(quote);
			} else if (c == '\\') {
				escapeStack++;
			} else {
				if (escapeStack > 0) {
					for (int j = -1; j < escapeStack; j++) {
						output.append('\\');
					}
				}
				switch (c) {
					case '\t' -> {
						output.append('\\');
						output.append('t');
					}
					case '\b' -> {
						output.append('\\');
						output.append('b');
					}
					case '\n' -> {
						output.append('\\');
						output.append('n');
					}
					case '\r' -> {
						output.append('\\');
						output.append('r');
					}
					case '\f' -> {
						output.append('\\');
						output.append('f');
					}
					default -> {
						output.append(c);
					}
				}
				escapeStack = 0;
			}
		}
		//value = value.replace("" + quote, "\\" + quote);
		output.append(quote);
	}

	public static String readQuoted(CharInput input, char quote) throws IOException {
		StringBuilder builder = null;
		while (true) {
			int p = input.getPos();
			int start = p;
			while (input.isAvailable(p, 1)) {
				int c = input.getCharAt(p++);

				if (c == quote) {
					input.setPos(p);
					int len = p - start - 1;
					if (builder == null) {
						return input.subString(start, start + len);
					} else {
						builder.append(input.getChars(start, len));
						return builder.toString();
					}
				} else if (c == '\\') {
					input.setPos(p);
					int len = p - start - 1;
					if (builder == null) {
						int estimatedLength = (len + 1) * 2;
						builder = new StringBuilder(Math.max(estimatedLength, 16));
					}
					builder.append(input.getChars(start, len));
					builder.append(readEscapeCharacter(input));
					p = input.getPos();
					start = p;
				}
			}

			if (builder == null) {
				int estimatedLength = (p - start) * 2;
				builder = new StringBuilder(Math.max(estimatedLength, 16));
			}
			builder.append(input.getChars(start, p - start));
			input.setPos(p);
			if (!input.isAvailable(p, 1)) {
				return builder.toString();
				//throw new IOException("Unterminated string");
			}
		}
	}


	public static char readEscapeCharacter(CharInput input) throws IOException {

		char escaped = input.getCurrentCharAntInc();
		switch (escaped) {
			case 'u':
				if (!input.isAvailable(4)) {
					throw new EndOfInputException("Unterminated escape sequence");
				}
				// Equivalent to Integer.parseInt(stringPool.get(buffer, pos, 4), 16);
				char result = 0;
				for (int i = input.getPos(), end = i + 4; i < end; i++) {
					char c = input.getCharAt(i);
					result <<= 4;
					if (c >= '0' && c <= '9') {
						result += (char) (c - '0');
					} else if (c >= 'a' && c <= 'f') {
						result += (char) (c - 'a' + 10);
					} else if (c >= 'A' && c <= 'F') {
						result += (char) (c - 'A' + 10);
					} else {
						throw new NumberFormatException("\\u" + input.subString(4));
					}
				}
				input.skip(4);
				return result;

			case 't':
				return '\t';

			case 'b':
				return '\b';

			case 'n':
				return '\n';

			case 'r':
				return '\r';

			case 'f':
				return '\f';

			case '\n', '\'', '"', '\\', '/':
				return escaped;
			default:
				// throw error when none of the above cases are matched
				throw new IOException("Invalid escape sequence");
		}
	}

	public static class PatternFormatter {

		private final List<String> parts = new ArrayList<>();
		private final List<String> keys = new ArrayList<>();

		private PatternFormatter(String pattern, String startSequence, String endSequence) {
			int i = pattern.indexOf(startSequence);
			final int ssl = startSequence.length();
			final int esl = endSequence.length();
			if (i == -1) {
				this.parts.add(pattern);
			} else {
				int c = 0;
				for (; i != -1; i = pattern.indexOf(startSequence, c + 1)) {
					this.parts.add(pattern.substring(c, i));
					c = pattern.indexOf(endSequence, i + ssl);
					this.keys.add(pattern.substring(i + ssl, c));
					c += esl;
				}
				this.parts.add(pattern.substring(c));
			}
		}

		public String assemble(Function<String, Object> mappingFunction) {
			if (parts.size() == 1) {
				return parts.get(0);
			}
			StringBuilder sb = new StringBuilder(parts.get(0));
			int i = 0;
			while (i < keys.size()) {
				Object value = mappingFunction.apply(keys.get(i));
				sb.append(value).append(parts.get(++i));
			}
			return sb.toString();
		}
	}

	public static class SequenceFormatter {

		private final List<String> parts = new ArrayList<>();

		private SequenceFormatter(String pattern, String delimiter) {
			int i = pattern.indexOf(delimiter);
			if (i == -1) {
				this.parts.add(pattern);
			} else {
				int delimiterLen = delimiter.length();
				int c = 0;
				for (; i != -1; i = pattern.indexOf(delimiter, c)) {
					this.parts.add(pattern.substring(c, i));
					c = i + delimiterLen;
				}
				this.parts.add(pattern.substring(c));
			}
		}

		public String assemble(Object... values) {
			if (parts.size() == 1) {
				return parts.get(0);
			}
			StringBuilder sb = new StringBuilder(parts.get(0));
			for (int i = 1; i < parts.size(); i++) {
				sb.append(values[i - 1]).append(parts.get(i));
			}
			return sb.toString();
		}
	}
}
