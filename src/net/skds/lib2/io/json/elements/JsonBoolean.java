package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.UniversalCodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.sosison.SosisonEntryType;

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

	@Override
	public JsonBoolean deepCopy() {
		return this;
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
		public JsonBoolean read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return FALSE;
			}
			boolean b = reader.readBoolean();
			return valueOf(b);
		}
	}
}
