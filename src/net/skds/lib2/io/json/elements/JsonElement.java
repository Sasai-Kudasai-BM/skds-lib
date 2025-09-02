package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.lang.reflect.Type;

public sealed interface JsonElement permits JsonBoolean, JsonElement.JsonNull, JsonArray, JsonNumber, JsonObject, JsonString {

	JsonElement NULL = new JsonNull();

	JsonElementType type();

	default JsonObject getAsJsonObject() {
		throw new UnsupportedOperationException();
	}

	default JsonArray getAsJsonArray() {
		throw new UnsupportedOperationException();
	}

	//String valueAsString();

	default boolean getAsBoolean() {
		throw new UnsupportedOperationException();
	}

	default String getAsString() {
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

	JsonElement deepCopy();

	default boolean isJsonPrimitive() {
		return this.type().isJsonPrimitive();
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

		@Override
		public JsonNull deepCopy() {
			return this;
		}
	}

	class Codec extends AbstractCodec<JsonElement> {

		private final UniversalCodec<JsonObject> objectCodec;
		private final UniversalCodec<JsonArray> arrayCodec;
		private final UniversalCodec<JsonString> stringCodec;
		private final UniversalCodec<JsonBoolean> booleanCodec;
		private final UniversalCodec<JsonNumber> numberCodec;

		public Codec(Type type, CodecRegistry registry) {
			super(type, registry);

			this.objectCodec = registry.getCodecIndirect(JsonObject.class);
			this.arrayCodec = registry.getCodecIndirect(JsonArray.class);
			this.stringCodec = registry.getCodecIndirect(JsonString.class);
			this.booleanCodec = registry.getCodecIndirect(JsonBoolean.class);
			this.numberCodec = registry.getCodecIndirect(JsonNumber.class);
		}

		@Override
		public void write(JsonElement value, UniversalWriter writer) throws IOException {
			switch (value.type()) {
				case BOOLEAN -> booleanCodec.write((JsonBoolean) value, writer);
				case OBJECT -> objectCodec.write((JsonObject) value, writer);
				case LIST -> arrayCodec.write((JsonArray) value, writer);
				case NUMBER -> numberCodec.write((JsonNumber) value, writer);
				case STRING -> stringCodec.write((JsonString) value, writer);
				case NULL -> writer.writeNull();
			}
		}

		@Override
		public JsonElement read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return JsonElement.NULL;
				}
				case BEGIN_OBJECT -> {
					return objectCodec.read(reader);
				}
				case BEGIN_LIST -> {
					return arrayCodec.read(reader);
				}
				case STRING -> {
					return stringCodec.read(reader);
				}
				case BOOLEAN -> {
					return booleanCodec.read(reader);
				}
				default -> {
					if (type.isNumber()) {
						return numberCodec.read(reader);
					}
					throw new ParseException("Unexpected token " + type);
				}
			}
		}
	}


}
