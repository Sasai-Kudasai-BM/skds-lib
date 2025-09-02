package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.UniversalCodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;
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

	@Override
	public JsonString deepCopy() {
		return this;
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
		public JsonString read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
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
