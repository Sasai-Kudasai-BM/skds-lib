package net.skds.lib2.io.json;

public abstract class WrappedJsonReaderImpl implements JsonReader {

	/*
	private final JsonElement input;

	private StackEntry stack;

	public WrappedJsonReaderImpl(JsonElement input) {
		this.input = input;
		this.stack = new StackEntry(null, input);
	}

	private void validateEntryType(JsonEntryType expected) throws IOException {
		JsonEntryType next = nextEntryType();
		if (next != expected) {
			throw new JsonReadException("Expected " + expected + " but next entry is " + next);
		}
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


	}

	@Override
	public void readDotDot() throws IOException {
	}

	private static class StackEntry {
		final StackEntry parent;
		final JsonElement element;
		final Iterator<Map.Entry<String, JsonElement>> mapIterator;
		final Iterator<JsonElement> listIterator;

		JsonEntryType nextElementType;
		JsonElement nextElement;
		String nextName;
		boolean first = true;

		private StackEntry(StackEntry parent, JsonElement element) {
			this.parent = parent;
			this.element = element;

			switch (element.type()) {
				case OBJECT -> {
					this.mapIterator = ((JsonObject) element).entrySet().iterator();
					this.listIterator = null;
				}
				case ARRAY -> {
					this.listIterator = ((JsonArray) element).iterator();
					this.mapIterator = null;
				}
				default -> {
					this.listIterator = null;
					this.mapIterator = null;
				}
			}
			nextElement();
		}

		JsonElement nextElement() {
			JsonElement ne;
			switch (element.type()) {
				case OBJECT -> {
					if (mapIterator.hasNext()) {
						var e = mapIterator.next();
						ne = e.getValue();
						nextName = e.getKey();
						nextElementType = ne.type().getBeginEntryType();
					} else {
						nextName = null;
						ne = null;
						nextElementType = JsonEntryType.END_OBJECT;
					}
				}
				case ARRAY -> {
					nextName = null;
					if (listIterator.hasNext()) {
						ne = listIterator.next();
						nextElementType = ne.type().getBeginEntryType();
					} else {
						ne = null;
						nextElementType = JsonEntryType.END_ARRAY;
					}
				}
				default -> {
					nextName = null;
					if (first) {
						ne = element;
						nextElementType = element.type().getBeginEntryType();
					} else {
						nextElementType = null;
						ne = null;
					}
				}
			}

			nextElement = ne;
			first = false;
			return ne;
		}

		String readName() {
			String n = nextName;
			nextName = null;
			return n;
		}

		JsonEntryType nextEntryType() throws JsonReadException {
			if (nextElementType == null) {
				throw new JsonReadException("NextElementType is null");
			}
			if (nextName != null) {
				return JsonEntryType.STRING;
			}
			return nextElementType;
		}
	}

	//*/
}
