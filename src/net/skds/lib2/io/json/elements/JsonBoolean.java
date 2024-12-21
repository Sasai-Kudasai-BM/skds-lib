package net.skds.lib2.io.json.elements;

public record JsonBoolean(boolean value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.BOOLEAN;
	}

	//@Override
	//public String valueAsString() {
	//	return String.valueOf(value);
	//}

	@Override
	public boolean getAsBoolean() {
		return value;
	}
}
