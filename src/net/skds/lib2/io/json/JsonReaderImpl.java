package net.skds.lib2.io.json;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.EndOfInputException;
import net.skds.lib2.utils.Numbers;

import java.io.IOException;

public class JsonReaderImpl implements JsonReader {

	private final CharInput input;

	private int pos;
	private JsonEntryType lastReadEntryType;
	private Object cachedValue;
	private int valueEnd;


	public JsonReaderImpl(CharInput input) throws IOException {
		this.input = input;
		beginObject();
	}

	private void validateEntryType(JsonEntryType expected) throws IOException {
		JsonEntryType next = nextEntryType();
		if (next != expected) {
			throw new JsonReadException("Expected " + expected + " but next entry is " + next);
		}
	}

	private char readEscapeCharacter() throws IOException {

		char escaped = input.getCharAt(pos++);
		switch (escaped) {
			case 'u':
				if (!input.isAvailable(pos, 4)) {
					throw new EndOfInputException("Unterminated escape sequence");
				}
				// Equivalent to Integer.parseInt(stringPool.get(buffer, pos, 4), 16);
				char result = 0;
				for (int i = pos, end = i + 4; i < end; i++) {
					char c = input.getCharAt(i);
					result <<= 4;
					if (c >= '0' && c <= '9') {
						result += (char) (c - '0');
					} else if (c >= 'a' && c <= 'f') {
						result += (char) (c - 'a' + 10);
					} else if (c >= 'A' && c <= 'F') {
						result += (char) (c - 'A' + 10);
					} else {
						throw new NumberFormatException("\\u" + input.subString(pos, pos + 4));
					}
				}
				pos += 4;
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

	private void skipWhitespaces() throws IOException {
		while (true) {
			char next = input.getCharAt(pos);
			switch (next) {
				case 0x0D, '\t', ' ', '\n' -> pos++;
				default -> {
					return;
				}
			}
		}
	}

	private int findEndOfValue() throws IOException {
		int p = pos;
		while (input.isAvailable(p, 1)) {
			char next = input.getCharAt(p);
			switch (next) {
				case 0x0D, '\t', ' ', '\n', '}', ']', ',' -> {
					return p;
				}
				default -> p++;
			}
		}
		throw new EndOfInputException("Unfinished value");
	}

	private void resetLastEntry() {
		lastReadEntryType = null;
		cachedValue = null;
	}

	@Override
	public String readName() throws IOException {
		validateEntryType(JsonEntryType.STRING);
		char next = input.getCharAt(pos++);
		if (next != '"') {
			throw unexpectedCharacter(next, pos - 1);
		}
		String name = readQuoted('"');
		skipWhitespaces();
		next = input.getCharAt(pos++);
		if (next != ':') {
			throw unexpectedCharacter(next, pos - 1);
		}
		resetLastEntry();
		return name;
	}

	@Override
	public String readString() throws IOException {
		validateEntryType(JsonEntryType.STRING);
		char next = input.getCharAt(pos++);
		if (next != '"') {
			throw unexpectedCharacter(next, pos - 1);
		}
		resetLastEntry();
		return readQuoted('"');
	}

	@Override
	public Number readNumber() throws IOException {
		validateEntryType(JsonEntryType.NUMBER);
		pos = valueEnd;
		Number n = (Number) cachedValue;
		resetLastEntry();
		return n;
	}

	@Override
	public void beginObject() throws IOException {
		validateEntryType(JsonEntryType.BEGIN_OBJECT);
		pos++;
		resetLastEntry();
	}

	@Override
	public void endObject() throws IOException {
		validateEntryType(JsonEntryType.END_OBJECT);
		pos++;
		resetLastEntry();
	}

	@Override
	public void beginList() throws IOException {
		validateEntryType(JsonEntryType.BEGIN_LIST);
		pos++;
		resetLastEntry();
	}

	@Override
	public void endList() throws IOException {
		validateEntryType(JsonEntryType.END_LIST);
		pos++;
		resetLastEntry();
	}

	@Override
	public void skipNull() throws IOException {
		validateEntryType(JsonEntryType.NULL);
		pos = valueEnd;
		resetLastEntry();
	}

	@Override
	public boolean readBoolean() throws IOException {
		validateEntryType(JsonEntryType.BOOLEAN);
		pos = valueEnd;
		Boolean b = (Boolean) cachedValue;
		resetLastEntry();
		return b;
	}

	private String readQuoted(char quote) throws IOException {
		StringBuilder builder = null;
		while (true) {
			int p = pos;
			int start = p;
			while (input.isAvailable(p, 1)) {
				int c = input.getCharAt(p++);

				if (c == quote) {
					pos = p;
					int len = p - start - 1;
					if (builder == null) {
						return input.subString(start, start + len);
					} else {
						builder.append(input.getChars(start, len));
						return builder.toString();
					}
				} else if (c == '\\') {
					pos = p;
					int len = p - start - 1;
					if (builder == null) {
						int estimatedLength = (len + 1) * 2;
						builder = new StringBuilder(Math.max(estimatedLength, 16));
					}
					builder.append(input.getChars(start, len));
					builder.append(readEscapeCharacter());
					p = pos;
					start = p;
				}
			}

			if (builder == null) {
				int estimatedLength = (p - start) * 2;
				builder = new StringBuilder(Math.max(estimatedLength, 16));
			}
			builder.append(input.getChars(start, p - start));
			pos = p;
			if (input.isAvailable(p, 1)) {
				throw new JsonReadException("Unterminated string");
			}
		}
	}

	private static JsonReadException unexpectedCharacter(char c, int pos) {
		return new JsonReadException("Unexpected character \\u" + Integer.toHexString(c).toUpperCase() + " '" + c + "' at " + pos);
	}


	@Override
	public JsonEntryType nextEntryType() throws IOException {
		JsonEntryType rt = lastReadEntryType;
		if (rt != null) {
			return rt;
		}
		cachedValue = null;
		boolean seenDivider = false;
		while (rt == null) {
			skipWhitespaces();
			char next = input.getCharAt(pos);
			switch (next) {
				case '{' -> rt = JsonEntryType.BEGIN_OBJECT;
				case '}' -> rt = JsonEntryType.END_OBJECT;
				case '[' -> rt = JsonEntryType.BEGIN_LIST;
				case ']' -> rt = JsonEntryType.END_LIST;
				case '"' -> rt = JsonEntryType.STRING;

				case ',' -> {
					if (seenDivider) throw unexpectedCharacter(',', pos);
					seenDivider = true;
					pos++;
				}
				default -> {
					int end = findEndOfValue();
					String value = input.subString(pos, end);

					switch (value) {
						case "null" -> rt = JsonEntryType.NULL;
						case "false" -> {
							cachedValue = Boolean.FALSE;
							rt = JsonEntryType.BOOLEAN;
						}
						case "true" -> {
							cachedValue = Boolean.TRUE;
							rt = JsonEntryType.BOOLEAN;
						}
						default -> {
							try {
								cachedValue = Numbers.parseNumber(value);
								rt = JsonEntryType.NUMBER;
							} catch (NumberFormatException e) {
								throw new JsonReadException("Unable to parse number \"" + value + "\"");
							}
						}
					}
					valueEnd = end;
				}
			}
		}
		lastReadEntryType = rt;
		return rt;
	}

}
