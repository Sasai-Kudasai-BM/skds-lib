package net.skds.lib2.io.json.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.json.JsonEntryType;

@AllArgsConstructor
public enum JsonElementType {
	BOOLEAN(JsonEntryType.BOOLEAN),
	OBJECT(JsonEntryType.BEGIN_OBJECT),
	ARRAY(JsonEntryType.BEGIN_ARRAY),
	NUMBER(JsonEntryType.NUMBER),
	STRING(JsonEntryType.STRING),
	NULL(JsonEntryType.NULL);

	@Getter
	private final JsonEntryType beginEntryType;


}
