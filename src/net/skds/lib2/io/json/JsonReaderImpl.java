package net.skds.lib2.io.json;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.EndOfInputException;
import net.skds.lib2.utils.Numbers;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;

public final class JsonReaderImpl implements JsonReader {

	private final CharInput input;

	//private int pos;
	private JsonEntryType lastReadEntryType;
	private Object cachedValue;
	private int valueEnd;


	public JsonReaderImpl(CharInput input) throws IOException {
		this.input = input;
	}

	private void validateEntryType(JsonEntryType expected) throws IOException {
		JsonEntryType next = nextEntryType();
		if (next != expected) {
			throw new JsonReadException("Expected " + expected + " but next entry is " + next);
		}
	}

	private void skipWhitespaces() throws IOException {
		while (true) {
			char next = input.getCurrentChar();
			switch (next) {
				case 0x0D, '\t', ' ', '\n' -> input.skip(1);
				default -> {
					return;
				}
			}
		}
	}

	private int findEndOfValue() throws IOException {
		int p = input.getPos();
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
		char next = input.getCurrentCharAntInc();
		if (next != '"') {
			throw unexpectedCharacter(next, input.getPos() - 1);
		}
		String name = StringUtils.readQuoted(input, '"');
		skipWhitespaces();
		next = input.getCurrentCharAntInc();
		if (next != ':') {
			throw unexpectedCharacter(next, input.getPos() - 1);
		}
		resetLastEntry();
		return name;
	}

	@Override
	public String readString() throws IOException {
		validateEntryType(JsonEntryType.STRING);
		char next = input.getCurrentCharAntInc();
		if (next != '"') {
			throw unexpectedCharacter(next, input.getPos() - 1);
		}
		resetLastEntry();
		return StringUtils.readQuoted(input, '"');
	}

	@Override
	public Number readNumber() throws IOException {
		validateEntryType(JsonEntryType.NUMBER);
		input.setPos(valueEnd);
		Number n = (Number) cachedValue;
		resetLastEntry();
		return n;
	}

	@Override
	public void beginObject() throws IOException {
		validateEntryType(JsonEntryType.BEGIN_OBJECT);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void endObject() throws IOException {
		validateEntryType(JsonEntryType.END_OBJECT);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void beginArray() throws IOException {
		validateEntryType(JsonEntryType.BEGIN_ARRAY);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void endArray() throws IOException {
		validateEntryType(JsonEntryType.END_ARRAY);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void skipNull() throws IOException {
		validateEntryType(JsonEntryType.NULL);
		input.setPos(valueEnd);
		resetLastEntry();
	}

	@Override
	public boolean readBoolean() throws IOException {
		validateEntryType(JsonEntryType.BOOLEAN);
		input.setPos(valueEnd);
		Boolean b = (Boolean) cachedValue;
		resetLastEntry();
		return b;
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
			char next = input.getCurrentChar();
			switch (next) {
				case '{' -> rt = JsonEntryType.BEGIN_OBJECT;
				case '}' -> rt = JsonEntryType.END_OBJECT;
				case '[' -> rt = JsonEntryType.BEGIN_ARRAY;
				case ']' -> rt = JsonEntryType.END_ARRAY;
				case '"' -> rt = JsonEntryType.STRING;

				case ',' -> {
					if (seenDivider) throw unexpectedCharacter(',', input.getPos());
					seenDivider = true;
					input.skip(1);
				}
				default -> {
					int end = findEndOfValue();
					String value = input.subString(input.getPos(), end);

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
