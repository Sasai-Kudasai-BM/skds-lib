package net.skds.lib2.io.json;

import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.json.elements.JsonArray;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.elements.JsonElementType;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.Numbers;

import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class WrappedJsonReaderImpl implements UniversalReader {
	//*
	private StackEntry stack;
	private final JsonElement input;

	public WrappedJsonReaderImpl(JsonElement input) {
		this.input = input;
		//this.stack = new StackEntry(null, input);
	}


	private void validateEntryType(SosisonEntryType expected) throws IOException {
		SosisonEntryType next = nextEntryType();
		if (next != expected) {
			throw new ParseException("Expected " + expected + " but next entry is " + next);
		}
	}

	@Override
	public String readName() throws IOException {
		validateEntryType(SosisonEntryType.STRING);
		return stack.readName();
	}

	@Override
	public String readString() throws IOException {
		validateEntryType(SosisonEntryType.STRING);
		String n = stack.readName();
		if (n != null) {
			return n;
		}
		return nextElement().getAsString();
	}

	@Override
	public byte[] readByteArray() throws IOException {
		switch (nextEntryType()) {
			case STRING -> {
				return Base64.getDecoder().decode(readString());
			}
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.ByteGrowingArray array = new ArrayUtils.ByteGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readNumber().byteValue());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	@Override
	public short[] readShortArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.ShortGrowingArray array = new ArrayUtils.ShortGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readNumber().shortValue());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected LIST or NULL but next entry is " + nextEntryType());
		}
	}

	@Override
	public char[] readCharArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.CharGrowingArray array = new ArrayUtils.CharGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					switch (et) {
						case NULL -> {
							skipNull();
							array.add((char) 0);
						}
						case STRING -> {
							String cs = readString();
							if (cs.length() == 1) {
								array.add(cs.charAt(0));
							} else {
								throw new ParseException("Unexpected char " + cs);
							}
						}
						default -> {
							if (et.isNumber()) {
								array.add((char) readInt());
							} else
								throw new ParseException("Unexpected token " + et);
						}
					}
					array.add((char) readInt());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	@Override
	public int[] readIntArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.IntGrowingArray array = new ArrayUtils.IntGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readInt());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	@Override
	public long[] readLongArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.LongGrowingArray array = new ArrayUtils.LongGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readLong());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	@Override
	public float[] readFloatArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.FloatGrowingArray array = new ArrayUtils.FloatGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readFloat());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}

	@Override
	public double[] readDoubleArray() throws IOException {
		switch (nextEntryType()) {
			case NULL -> {
				skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				beginList();
				ArrayUtils.DoubleGrowingArray array = new ArrayUtils.DoubleGrowingArray(16);
				SosisonEntryType et;
				while ((et = nextEntryType()) != SosisonEntryType.END_LIST) {
					if (!et.isNumber()) {
						throw new ParseException("Non-number member in primitive array");
					}
					array.add(readDouble());
				}
				endList();
				return array.getArray();
			}
			default -> throw new ParseException("Expected STRING, LIST or NULL but next entry is " + nextEntryType());
		}
	}


	@Override
	public UUID readUUID() throws IOException {
		return UUID.fromString(readString());
	}

	@Override
	public Number readNumber() throws IOException {
		Number n;
		SosisonEntryType et = nextEntryType();
		switch (et) {
			case NULL -> {
				skipNull();
				return Numbers.ZERO;
			}
			case STRING -> {
				return Numbers.parseNumber(readString());
			}
			default -> {
				if (et.isNumber()) {
					n = nextElement().getAsNumber();
				} else
					throw new ParseException("Expected NUMBER, STRING or NULL but next entry is " + nextEntryType());
			}
		}
		return n;
	}

	@Override
	public void beginObject() throws IOException {
		validateEntryType(SosisonEntryType.BEGIN_OBJECT);
		pushStack();
	}

	@Override
	public void endObject() throws IOException {
		validateEntryType(SosisonEntryType.END_OBJECT);
		popStack();
	}

	@Override
	public void beginList() throws IOException {
		validateEntryType(SosisonEntryType.BEGIN_LIST);
		pushStack();
	}

	@Override
	public void endList() throws IOException {
		validateEntryType(SosisonEntryType.END_LIST);
		popStack();
	}

	@Override
	public void skipNull() throws IOException {
		switch (nextEntryType()) {
			case NULL -> nextElement();
			case STRING -> {
				String s = readString();
				if (!s.equalsIgnoreCase("null")) {
					throw new ParseException("Expected \"null\" string but got \"" + s + "\"");
				}
			}
			default -> throw new ParseException("Expected STRING or NULL but next entry is " + nextEntryType());
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
			default -> throw new ParseException("Expected STRING or BOOLEAN but next entry is " + nextEntryType());
		}
		return b;
	}

	@Override
	public SosisonEntryType nextEntryType() throws IOException {
		StackEntry s = stack;
		if (s == null) {
			JsonElementType jt = input.type();
			if (jt == JsonElementType.NUMBER) {
				return SosisonEntryType.number(input.getAsNumber());
			}
			return jt.getBeginEntryType();
		}
		return s.nextEntryType();
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

		SosisonEntryType nextElementType;
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
				case LIST -> {
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

						JsonElementType jt = ne.type();
						if (jt == JsonElementType.NUMBER) {
							nextElementType = SosisonEntryType.number(ne.getAsNumber());
						} else {
							nextElementType = jt.getBeginEntryType();
						}
					} else {
						nextName = null;
						ne = null;
						nextElementType = SosisonEntryType.END_OBJECT;
					}
				}
				case LIST -> {
					nextName = null;
					if (listIterator.hasNext()) {
						ne = listIterator.next();
						JsonElementType jt = ne.type();
						if (jt == JsonElementType.NUMBER) {
							nextElementType = SosisonEntryType.number(ne.getAsNumber());
						} else {
							nextElementType = jt.getBeginEntryType();
						}
					} else {
						ne = null;
						nextElementType = SosisonEntryType.END_LIST;
					}
				}
				default -> {
					nextName = null;
					if (first) {
						ne = element;
						JsonElementType jt = ne.type();
						if (jt == JsonElementType.NUMBER) {
							nextElementType = SosisonEntryType.number(ne.getAsNumber());
						} else {
							nextElementType = jt.getBeginEntryType();
						}
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

		SosisonEntryType nextEntryType() throws ParseException {
			if (nextElementType == null) {
				throw new ParseException("NextElementType is null");
			}
			if (nextName != null) {
				return SosisonEntryType.STRING;
			}
			return nextElementType;
		}
	}

	//*/
}
