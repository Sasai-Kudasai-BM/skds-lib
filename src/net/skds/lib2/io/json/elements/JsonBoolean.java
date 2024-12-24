package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.util.Objects;

public record JsonBoolean(boolean value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.BOOLEAN;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public boolean getAsBoolean() {
		return value;
	}

	public static final class Codec extends JsonCodec<JsonBoolean> {

		public Codec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(JsonBoolean value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeBoolean(false);
				return;
			}
			writer.writeBoolean(value.getAsBoolean());
		}

		@Override
		public JsonBoolean read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (Objects.requireNonNull(type) == JsonEntryType.BOOLEAN) {
				boolean b = reader.readBoolean();
				return new JsonBoolean(b);
			}
			throw new JsonReadException("Unexpected token " + type);
		}
	}
}
