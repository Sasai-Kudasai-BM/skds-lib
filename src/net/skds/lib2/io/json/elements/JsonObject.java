package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;

import java.io.IOException;
import java.util.HashMap;

public final class JsonObject extends HashMap<String, JsonElement> implements JsonElement {

	public JsonObject() {
	}

	public JsonObject(int initialSize) {
		super(initialSize);
	}

	@Override
	public JsonElementType type() {
		return JsonElementType.OBJECT;
	}

	@Override
	public JsonObject getAsJsonObject() {
		return this;
	}

	public static class Codec implements JsonCodec<JsonObject> {

		@Override
		public void serialize(JsonObject value, JsonWriter writer) throws IOException {

		}

		@Override
		public JsonObject deserialize(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					return null;
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					JsonObject jo = new JsonObject();
					while ((type = reader.nextEntryType()) != JsonEntryType.END_OBJECT) {
						
					}
					reader.endObject();
					return jo;
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}
}
