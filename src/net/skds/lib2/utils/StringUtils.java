package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;
import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.EndOfInputException;

import java.io.IOException;

@UtilityClass
public class StringUtils {

	public static String quote(String s) {
		return '"' + s.replace("\"", "\\\"") + '"';
	}

	public static String unquote(String s) {
		return s.substring(1, s.length() - 1).replace("\\\"", "\"");
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
			if (input.isAvailable(p, 1)) {
				throw new IOException("Unterminated string");
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

}
