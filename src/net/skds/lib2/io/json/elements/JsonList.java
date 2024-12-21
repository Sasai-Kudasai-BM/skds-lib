package net.skds.lib2.io.json.elements;

import java.util.ArrayList;

public final class JsonList extends ArrayList<JsonElement> implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.LIST;
	}

	//@Override
	//public String valueAsString() {
	//	StringJoiner sj = ne
	//	return null;
	//}

	public boolean add(Number value) {
		return super.add(new JsonNumber(value));
	}

	public boolean add(boolean value) {
		return super.add(new JsonBoolean(value));
	}

	public boolean add(String value) {
		return super.add(new JsonString(value));
	}

	public boolean addNull() {
		return super.add(JsonElement.NULL);
	}
}
