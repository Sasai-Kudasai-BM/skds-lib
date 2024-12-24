package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;

public record JsonString(String value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.STRING;
	}

	public static final class Codec implements JsonCodec<JsonString> {

		public Codec(JsonCodecRegistry registry) {
		}

		@Override
		public void serialize(JsonString value, JsonWriter writer) throws IOException {
			writer.writeString(value.value);
		}

		@Override
		public JsonString deserialize(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					String s = reader.readString();
					return new JsonString(s);
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	@Override
	public String toString() {
		return StringUtils.quote(value);
	}

	//@Override
	//public String valueAsString() {
	//	return "\"" + value.replace("\"", "\\\"") + "\"";
	//}
}
