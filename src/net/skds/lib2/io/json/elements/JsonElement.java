package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.lang.reflect.Type;

public sealed interface JsonElement permits JsonBoolean, JsonElement.JsonNull, JsonArray, JsonNumber, JsonObject, JsonString {

	JsonElement NULL = new JsonNull();

	JsonElementType type();

	default JsonObject getAsJsonObject() {
		throw new UnsupportedOperationException();
	}

	default JsonArray getAsJsonList() {
		throw new UnsupportedOperationException();
	}

	//String valueAsString();

	default boolean getAsBoolean() {
		throw new UnsupportedOperationException();
	}

	default Number getAsNumber() {
		throw new UnsupportedOperationException();
	}

	default int getAsInt() {
		return getAsNumber().intValue();
	}

	default float getAsFloat() {
		return getAsNumber().floatValue();
	}

	default long getAsLong() {
		return getAsNumber().longValue();
	}

	default double getAsDouble() {
		return getAsNumber().doubleValue();
	}

	final class JsonNull implements JsonElement {
		@Override
		public JsonElementType type() {
			return JsonElementType.NULL;
		}

		@Override
		public String toString() {
			return "null";
		}
	}

	class Codec extends AbstractJsonCodec<JsonElement> {

		private final JsonCodec<JsonObject> objectCodec;
		private final JsonCodec<JsonArray> arrayCodec;
		private final JsonCodec<JsonString> stringCodec;
		private final JsonCodec<JsonBoolean> booleanCodec;
		private final JsonCodec<JsonNumber> numberCodec;

		public Codec(Type type, JsonCodecRegistry registry) {
			super(registry);

			this.objectCodec = registry.getCodecIndirect(JsonObject.class);
			this.arrayCodec = registry.getCodecIndirect(JsonArray.class);
			this.stringCodec = registry.getCodecIndirect(JsonString.class);
			this.booleanCodec = registry.getCodecIndirect(JsonBoolean.class);
			this.numberCodec = registry.getCodecIndirect(JsonNumber.class);
		}

		@Override
		public void write(JsonElement value, JsonWriter writer) throws IOException {
			switch (value.type()) {

				case BOOLEAN -> booleanCodec.write((JsonBoolean) value, writer);
				case OBJECT -> objectCodec.write((JsonObject) value, writer);
				case ARRAY -> arrayCodec.write((JsonArray) value, writer);
				case NUMBER -> numberCodec.write((JsonNumber) value, writer);
				case STRING -> stringCodec.write((JsonString) value, writer);
				case NULL -> writer.writeNull();
			}
		}

		@Override
		public JsonElement read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return JsonElement.NULL;
				}
				case BEGIN_OBJECT -> {
					return objectCodec.read(reader);
				}
				case BEGIN_ARRAY -> {
					return arrayCodec.read(reader);
				}
				case STRING -> {
					return stringCodec.read(reader);
				}
				case BOOLEAN -> {
					return booleanCodec.read(reader);
				}
				case NUMBER -> {
					return numberCodec.read(reader);
				}

				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}
}
