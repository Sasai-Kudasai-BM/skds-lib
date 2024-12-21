package net.skds.lib2.io.json.elements;

public sealed interface JsonElement permits JsonBoolean, JsonElement.JsonNull, JsonList, JsonNumber, JsonObject, JsonString {

	JsonElement NULL = new JsonNull();

	JsonElementType type();

	default JsonObject getAsJsonObject() {
		throw new UnsupportedOperationException();
	}

	default JsonList getAsJsonList() {
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

		//@Override
		//public String valueAsString() {
		//	return "null";
		//}
	}
}
