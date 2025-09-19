package net.skds.lib2.io.json;

import net.skds.lib2.io.chars.CharInput;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.exception.EndOfInputException;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.utils.Numbers;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;
import java.util.UUID;

public final class JsonReaderImpl implements UniversalReader {

	private final CharInput input;
	private final UniversalCodec<JsonElement> skipCodec;

	//private int pos;
	private SosisonEntryType lastReadEntryType;
	private Object cachedValue;
	private int valueEnd;

	public JsonReaderImpl(CharInput input, CodecRegistry registry) {
		this.input = input;
		this.skipCodec = registry.getCodec(JsonElement.class);
	}


	private void validateEntryType(SosisonEntryType expected) throws IOException {
		SosisonEntryType next = this.nextEntryType();
		if (next != expected) {
			throw new ParseException("Expected " + expected + " but next entry is " + next);
		}
	}

	private void skipWhitespaces() throws IOException {
		while (true) {
			char next = input.getCurrentChar();
			switch (next) {
				case '/' -> {
					char n2 = input.getNextChar();
					switch (n2) {
						case '/' -> skipSingleLineComment();
						case '*' -> skipMultiLineComment();
						default -> throw new ParseException("Unexpected slash");
					}
				}
				case 0x0D, '\t', ' ', '\n' -> input.skip(1);
				default -> {
					return;
				}
			}
		}
	}

	private void skipSingleLineComment() throws IOException {
		while (true) {
			char next = input.getNextChar();
			if (next == '\n') {
				return;
			}
		}
	}

	private void skipMultiLineComment() throws IOException {
		while (true) {
			char next = input.getNextChar();
			if (next == '*') {
				char n2 = input.getNextChar();
				if (n2 == '/') {
					input.skip(1);
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
		validateEntryType(SosisonEntryType.STRING);
		char next = input.getCurrentCharAntInc();
		if (next != '"') {
			throw unexpectedCharacter(next, input.getPos() - 1);
		}
		String name = StringUtils.readQuoted(input, '"');
		skipWhitespaces();
		char next2 = input.getCurrentCharAntInc();
		if (next2 != ':') {
			throw unexpectedCharacter(next2, input.getPos() - 1);
		}
		resetLastEntry();
		return name;
	}

	@Override
	public String readString() throws IOException {
		validateEntryType(SosisonEntryType.STRING);
		char next = input.getCurrentCharAntInc();
		if (next != '"') {
			throw unexpectedCharacter(next, input.getPos() - 1);
		}
		resetLastEntry();
		return StringUtils.readQuoted(input, '"');
	}


	@Override
	public UUID readUUID() throws IOException {
		SosisonEntryType type = this.nextEntryType();
		switch (type) {
			case NULL -> {
				skipNull();
				return null;
			}
			case STRING -> {
				return UUID.fromString(readString());
			}
			case BEGIN_LIST -> {
				beginList();
				long m = readLong();
				long l = readLong();
				endList();
				return new UUID(m, l);
			}
			default -> throw new ParseException("Unexpected token " + type);
		}
	}

	@Override
	public Number readNumber() throws IOException {
		Number n;
		SosisonEntryType et = this.nextEntryType();
		switch (et) {
			case NULL -> {
				return Numbers.ZERO;
			}
			case STRING -> {
				return Numbers.parseNumber(readString());
			}
			default -> {
				if (et.isNumber()) {
					input.setPos(valueEnd);
					n = (Number) cachedValue;
					resetLastEntry();
				} else
					throw new ParseException("Expected NUMBER, STRING or NULL but next entry is " + this.nextEntryType());
			}
		}
		return n;
	}

	@Override
	public void beginObject() throws IOException {
		validateEntryType(SosisonEntryType.BEGIN_OBJECT);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void endObject() throws IOException {
		validateEntryType(SosisonEntryType.END_OBJECT);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void beginList() throws IOException {
		validateEntryType(SosisonEntryType.BEGIN_LIST);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void endList() throws IOException {
		validateEntryType(SosisonEntryType.END_LIST);
		input.skip(1);
		resetLastEntry();
	}

	@Override
	public void skipNull() throws IOException {
		switch (this.nextEntryType()) {
			case NULL -> {
				input.setPos(valueEnd);
				resetLastEntry();
			}
			case STRING -> {
				String s = readString();
				if (!s.equalsIgnoreCase("null")) {
					throw new ParseException("Expected \"null\" string but got \"" + s + "\"");
				}
			}
			default -> throw new ParseException("Expected STRING or NULL but next entry is " + this.nextEntryType());
		}
	}

	@Override
	public void skipValue() throws IOException {
		skipCodec.read(this);
	}

	@Override
	public boolean readBoolean() throws IOException {
		Boolean b;
		switch (this.nextEntryType()) {
			case BOOLEAN -> {
				input.setPos(valueEnd);
				b = (Boolean) cachedValue;
				resetLastEntry();
			}
			case STRING -> {
				return Boolean.parseBoolean(readString());
			}
			default -> throw new ParseException("Expected STRING or BOOLEAN but next entry is " + this.nextEntryType());
		}
		return b;
	}


	private static ParseException unexpectedCharacter(char c, int pos) {
		return new ParseException("Unexpected character \\u" + Integer.toHexString(c).toUpperCase() + " '" + c + "' at " + pos);
	}


	@Override
	public SosisonEntryType nextEntryType() throws IOException {
		SosisonEntryType rt = lastReadEntryType;
		if (rt != null) {
			return rt;
		}
		cachedValue = null;
		boolean seenDivider = false;
		while (rt == null) {
			skipWhitespaces();
			char next = input.getCurrentChar();
			switch (next) {
				case '{' -> rt = SosisonEntryType.BEGIN_OBJECT;
				case '}' -> rt = SosisonEntryType.END_OBJECT;
				case '[' -> rt = SosisonEntryType.BEGIN_LIST;
				case ']' -> rt = SosisonEntryType.END_LIST;
				case '"' -> rt = SosisonEntryType.STRING;

				case ',' -> {
					if (seenDivider) throw unexpectedCharacter(',', input.getPos());
					seenDivider = true;
					input.skip(1);
				}
				default -> {
					int end = findEndOfValue();
					String value = input.subString(input.getPos(), end);

					switch (value) {
						case "null" -> rt = SosisonEntryType.NULL;
						case "false" -> {
							cachedValue = Boolean.FALSE;
							rt = SosisonEntryType.BOOLEAN;
						}
						case "true" -> {
							cachedValue = Boolean.TRUE;
							rt = SosisonEntryType.BOOLEAN;
						}
						default -> {
							try {
								cachedValue = Numbers.parseNumber(value);
								rt = SosisonEntryType.number((Number) cachedValue);
							} catch (NumberFormatException e) {
								throw new ParseException("Unable to parse number \"" + value + "\"");
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
