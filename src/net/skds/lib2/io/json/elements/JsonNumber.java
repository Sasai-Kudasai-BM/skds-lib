package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonElementType;

public record JsonNumber(Number value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.NUMBER;
	}

	@Override
	public Number getAsNumber() {
		return value;
	}
}
