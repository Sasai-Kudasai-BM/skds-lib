package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;

public record JsonNumber(Number value) implements JsonElement {

	public static final JsonNumber ZERO = new JsonNumber(0);

	@Override
	public JsonElementType type() {
		return JsonElementType.NUMBER;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public Number getAsNumber() {
		return value;
	}

	public static final class Codec implements JsonCodec<JsonNumber> {

		public Codec(JsonCodecRegistry registry) {
		}

		@Override
		public void serialize(JsonNumber value, JsonWriter writer) throws IOException {

		}

		@Override
		public JsonNumber deserialize(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return ZERO;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return new JsonNumber(n);
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}
}
