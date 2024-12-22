package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;

public final class JsonArray extends ArrayList<JsonElement> implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.LIST;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",", "[", "]");
		for (JsonElement e : this) {
			sj.add(String.valueOf(e));
		}
		return sj.toString();
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

	public static class Codec implements JsonCodec<JsonArray> {

		private final JsonCodec<JsonElement> elementCodec;

		public Codec(JsonCodecRegistry registry) {
			this.elementCodec = registry.getCodec(JsonElement.class);
		}

		@Override
		public void serialize(JsonArray value, JsonWriter writer) throws IOException {

		}

		@Override
		public JsonArray deserialize(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					JsonArray ja = new JsonArray();
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						JsonElement e = elementCodec.deserialize(reader);
						ja.add(e);
					}
					reader.endArray();
					return ja;
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}
}
