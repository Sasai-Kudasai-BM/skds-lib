package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.json.JsonElementType;

import java.util.HashMap;

public final class JsonObject extends HashMap<String, JsonElement> implements JsonElement {
	@Override
	public JsonElementType type() {
		return JsonElementType.OBJECT;
	}

	@Override
	public JsonObject getAsJsonObject() {
		return this;
	}
}
