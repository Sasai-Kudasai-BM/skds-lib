package net.skds.lib2.io.json.elements;

public record JsonString(String value) implements JsonElement {

	@Override
	public JsonElementType type() {
		return JsonElementType.STRING;
	}

	//@Override
	//public String valueAsString() {
	//	return "\"" + value.replace("\"", "\\\"") + "\"";
	//}
}
