package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.exception.JsonReadException;
import net.skds.lib2.utils.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public record JsonString(String value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.STRING;
	}

	@Override
	public String getAsString() {
		return value;
	}

	public static final class Codec extends AbstractJsonCodec<JsonString> {

		public Codec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
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
