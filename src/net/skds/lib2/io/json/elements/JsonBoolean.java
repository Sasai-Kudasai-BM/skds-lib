package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.lang.reflect.Type;

public record JsonBoolean(boolean value) implements JsonElement {

	public static final JsonBoolean TRUE = new JsonBoolean(true);
	public static final JsonBoolean FALSE = new JsonBoolean(false);

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

	public static JsonBoolean valueOf(boolean b) {
		return b ? TRUE : FALSE;
	}

	public static final class Codec extends AbstractJsonCodec<JsonBoolean> {

		public Codec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
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
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return FALSE;
			}
			boolean b = reader.readBoolean();
			return valueOf(b);
		}
	}
}
