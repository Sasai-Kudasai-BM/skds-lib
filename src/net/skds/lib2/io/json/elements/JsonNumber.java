package net.skds.lib2.io.json.elements;

public record JsonNumber(Number value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.NUMBER;
	}

	//@Override
	//public String valueAsString() {
	//	return String.valueOf(value);
	//}

	@Override
	public Number getAsNumber() {
		return value;
	}
}
