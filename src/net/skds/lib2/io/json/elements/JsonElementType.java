package net.skds.lib2.io.json.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.json.JsonEntryType;

@AllArgsConstructor
public enum JsonElementType {
	BOOLEAN(JsonEntryType.BOOLEAN),
	OBJECT(JsonEntryType.BEGIN_OBJECT) {
		@Override
		public boolean isJsonPrimitive() {
			return false;
		}
	},
	ARRAY(JsonEntryType.BEGIN_ARRAY) {
		@Override
		public boolean isJsonPrimitive() {
			return false;
		}
	},
	NUMBER(JsonEntryType.NUMBER),
	STRING(JsonEntryType.STRING),
	NULL(JsonEntryType.NULL) {
		@Override
		public boolean isJsonPrimitive() {
			return false;
		}
	};

	@Getter
	private final JsonEntryType beginEntryType;

	public boolean isJsonPrimitive() {
		return true;
	}


}
