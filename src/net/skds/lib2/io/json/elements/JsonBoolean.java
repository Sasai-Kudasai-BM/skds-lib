package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonElementType;

public record JsonBoolean(boolean value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.BOOLEAN;
	}

	@Override
	public boolean getAsBoolean() {
		return value;
	}
}
