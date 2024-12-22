package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;

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

	class Codec implements JsonCodec<JsonElement> {

		private final JsonCodecRegistry registry;

		public Codec(JsonCodecRegistry registry) {
			this.registry = registry;
		}

		@Override
		public void serialize(JsonElement value, JsonWriter writer) throws IOException {

		}

		@Override
		public JsonElement deserialize(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					return null;
				}
				case BEGIN_OBJECT -> {
					JsonCodec<JsonObject> codec = registry.getCodec(JsonObject.class);
					return codec.deserialize(reader);
				}
				case BEGIN_ARRAY -> {
					JsonCodec<JsonArray> codec = registry.getCodec(JsonArray.class);
					return codec.deserialize(reader);
				}
				case STRING -> {
					JsonCodec<JsonString> codec = registry.getCodec(JsonString.class);
					return codec.deserialize(reader);
				}
				case BOOLEAN -> {
					JsonCodec<JsonBoolean> codec = registry.getCodec(JsonBoolean.class);
					return codec.deserialize(reader);
				}
				case NUMBER -> {
					JsonCodec<JsonNumber> codec = registry.getCodec(JsonNumber.class);
					return codec.deserialize(reader);
				}

				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}
}
