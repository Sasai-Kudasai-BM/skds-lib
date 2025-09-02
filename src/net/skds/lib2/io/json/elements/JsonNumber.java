package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.UniversalCodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.sosison.SosisonEntryType;

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

	@Override
	public JsonNumber deepCopy() {
		return this;
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
		public JsonNumber read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return ZERO;
			}
			Number n = reader.readNumber();
			return new JsonNumber(n);
		}
	}
}
