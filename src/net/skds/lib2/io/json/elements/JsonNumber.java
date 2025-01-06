package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.lang.reflect.Type;

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

	public static final class Codec extends AbstractJsonCodec<JsonNumber> {

		public Codec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(JsonNumber value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			Number n = value.getAsNumber();
			writer.writeRaw(n.toString());
		}

		@Override
		public JsonNumber read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return ZERO;
			}
			Number n = reader.readNumber();
			return new JsonNumber(n);
		}
	}
}
