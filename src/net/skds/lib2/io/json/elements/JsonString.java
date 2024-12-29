package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public record JsonString(String value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.STRING;
	}

	public static final class Codec extends JsonCodec<JsonString> {

		public Codec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(JsonString value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeString(value.value);
		}

		@Override
		public JsonString read(JsonReader reader) throws IOException {
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

}
