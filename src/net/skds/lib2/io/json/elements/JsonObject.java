package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.utils.StringUtils;

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		if (!isEmpty()) {
			forEach((k, v) -> sb.append(StringUtils.quote(k)).append(":").append(v).append(","));
			sb.setLength(sb.length() - 1);
		}
		sb.append('}');
		return sb.toString();
	}

	public static class Codec implements JsonCodec<JsonObject> {

		private final JsonCodec<JsonElement> elementCodec;

		public Codec(JsonCodecRegistry registry) {
			this.elementCodec = registry.getCodec(JsonElement.class);
		}

		@Override
		public void serialize(JsonObject value, JsonWriter writer) throws IOException {

		}

		@Override
		public JsonObject deserialize(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					JsonObject jo = new JsonObject();
					while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
						String name = reader.readName();
						JsonElement e = elementCodec.deserialize(reader);
						jo.put(name, e);
					}
					reader.endObject();
					return jo;
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}
}
