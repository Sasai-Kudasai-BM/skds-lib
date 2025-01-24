package net.skds.lib2.io.json;

import net.skds.lib2.io.json.elements.JsonArray;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.io.json.exception.JsonReadException;
import net.skds.lib2.utils.Numbers;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class WrappedJsonReaderImpl implements JsonReader {
	//*
	private StackEntry stack;
	private final JsonElement input;

	public WrappedJsonReaderImpl(JsonElement input) {
		this.input = input;
		//this.stack = new StackEntry(null, input);
	}

	private void validateEntryType(JsonEntryType expected) throws IOException {
		JsonEntryType next = nextEntryType();
		if (next != expected) {
			throw new JsonReadException("Expected " + expected + " but next entry is " + next);
		}
	}

	@Override
	public String readName() throws IOException {
		validateEntryType(JsonEntryType.STRING);
		return stack.readName();
	}

	@Override
	public String readString() throws IOException {
		validateEntryType(JsonEntryType.STRING);
		String n = stack.readName();
		{
			if (n != null) {
				return n;
			}
		}
		return nextElement().getAsString();
	}

	@Override
	public Number readNumber() throws IOException {
		Number n;
		switch (nextEntryType()) {
			case NUMBER -> {
				n = nextElement().getAsNumber();
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
		pushStack();
	}

	@Override
	public void endObject() throws IOException {
		validateEntryType(JsonEntryType.END_OBJECT);
		popStack();
	}

	@Override
	public void beginArray() throws IOException {
		validateEntryType(JsonEntryType.BEGIN_ARRAY);
		pushStack();
	}

	@Override
	public void endArray() throws IOException {
		validateEntryType(JsonEntryType.END_ARRAY);
		popStack();
	}

	@Override
	public void skipNull() throws IOException {
		switch (nextEntryType()) {
			case NULL -> nextElement();
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
	public void skipValue() {
		nextElement();
	}

	@Override
	public boolean readBoolean() throws IOException {
		boolean b;
		switch (nextEntryType()) {
			case BOOLEAN -> {
				b = nextElement().getAsBoolean();
			}
			case STRING -> {
				return Boolean.parseBoolean(readString());
			}
			default -> throw new JsonReadException("Expected STRING or BOOLEAN but next entry is " + nextEntryType());
		}
		return b;
	}

	@Override
	public JsonEntryType nextEntryType() throws IOException {
		StackEntry s = stack;
		if (s == null) {
			return input.type().getBeginEntryType();
		}
		return s.nextEntryType();
	}

	@Override
	public void readDotDot() {
	}

	private JsonElement nextElement() {
		StackEntry s = stack;
		if (s == null) {
			return input;
		}
		return s.nextElement();
	}

	private void pushStack() {
		StackEntry s = stack;
		if (s == null) {
			stack = new StackEntry(null, input);
			return;
		}
		stack = new StackEntry(s, s.nextElement());
	}

	private void popStack() {
		var s = stack;
		stack = s.parent;
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
				default -> throw new UnsupportedOperationException();
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

			var last = nextElement;
			nextElement = ne;
			first = false;
			return last;
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
