package net.skds.lib2.io.json.elements;

import lombok.NoArgsConstructor;
import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.StringJoiner;

@NoArgsConstructor
public final class JsonArray extends ArrayList<JsonElement> implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.LIST;
	}

	@Override
	public JsonArray getAsJsonArray() {
		return this;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "[", "]");
		for (JsonElement e : this) {
			sj.add(String.valueOf(e));
		}
		return sj.toString();
	}

	public JsonArray(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public boolean add(JsonElement jsonElement) {
		return super.add(jsonElement != null ? jsonElement : JsonElement.NULL);
	}

	public boolean add(Number value) {
		return super.add(new JsonNumber(value));
	}

	public boolean add(boolean value) {
		return super.add(new JsonBoolean(value));
	}

	public boolean add(String value) {
		return super.add(new JsonString(value));
	}

	public boolean addNull() {
		return super.add(JsonElement.NULL);
	}

	@Override
	public JsonArray deepCopy() {
		JsonArray array = new JsonArray();
		for (JsonElement jsonElement : this) {
			array.add(jsonElement.deepCopy());
		}
		return array;
	}

	public static class Codec extends AbstractCodec<JsonArray> {

		private final UniversalCodec<JsonElement> elementCodec;

		public Codec(Type type, CodecRegistry registry) {
			super(type, registry);
			this.elementCodec = registry.getCodecIndirect(JsonElement.class);
		}

		@Override
		public void write(JsonArray value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			if (value.size() > 1) {
				writer.lineBreakEnable(true);
			}
			for (var e : value) {
				elementCodec.write(e, writer);
			}
			writer.endList();
		}

		@Override
		public JsonArray read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					JsonArray ja = new JsonArray();
					int i = 0;
					while (reader.nextEntryType() != SosisonEntryType.END_LIST) {
						JsonElement e;
						try {
							e = elementCodec.read(reader);
						} catch (Exception ex) {
							throw new ParseException("Exception while read [" + i + "]", ex);
						}
						ja.add(e);
						i++;
					}
					reader.endList();
					return ja;
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}
	}
}
