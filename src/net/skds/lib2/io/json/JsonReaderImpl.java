package net.skds.lib2.io.json;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.EndOfInputException;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.exception.JsonReadException;
import net.skds.lib2.utils.Numbers;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;

public final class JsonReaderImpl implements JsonReader {

	private final CharInput input;
	private final JsonCodec<JsonElement> skipCodec;

	//private int pos;
	private JsonEntryType lastReadEntryType;
	private Object cachedValue;
	private int valueEnd;


	public JsonReaderImpl(CharInput input, JsonCodecRegistry registry) {
		this.input = input;
		this.skipCodec = registry.getCodec(JsonElement.class);
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
		readDotDot();
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
		Number n;
		switch (nextEntryType()) {
			case NUMBER -> {
				input.setPos(valueEnd);
				n = (Number) cachedValue;
				resetLastEntry();
			}
			case NULL -> {
				return Numbers.ZERO;
			}
			case STRING -> {
				return Numbers.parseNumber(readString());
			}
			default ->
					throw new JsonReadException("Expected NUMBER, STRING or NULL but next entry is " + nextEntryType());
		}
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
		switch (nextEntryType()) {
			case NULL -> {
				input.setPos(valueEnd);
				resetLastEntry();
			}
			case STRING -> {
				String s = readString();
				if (!s.equalsIgnoreCase("null")) {
					throw new JsonReadException("Expected \"null\" string but got \"" + s + "\"");
				}
			}
			default -> throw new JsonReadException("Expected STRING or NULL but next entry is " + nextEntryType());
		}
	}

	@Override
	public void skipValue() throws IOException {
		skipCodec.read(this);
	}

	@Override
	public boolean readBoolean() throws IOException {
		Boolean b;
		switch (nextEntryType()) {
			case BOOLEAN -> {
				input.setPos(valueEnd);
				b = (Boolean) cachedValue;
				resetLastEntry();
			}
			case STRING -> {
				return Boolean.parseBoolean(readString());
			}
			default -> throw new JsonReadException("Expected STRING or BOOLEAN but next entry is " + nextEntryType());
		}
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

	@Override
	public void readDotDot() throws IOException {
		skipWhitespaces();
		char next = input.getCurrentCharAntInc();
		if (next != ':') {
			throw unexpectedCharacter(next, input.getPos() - 1);
		}
	}

}
