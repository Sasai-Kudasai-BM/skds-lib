package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonElementType;

public record JsonString(String value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.STRING;
	}

	@Override
	public String getAsString() {
		return value;
	}
}
