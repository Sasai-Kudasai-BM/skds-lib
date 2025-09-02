package net.skds.lib2.io.json.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.skds.lib2.io.sosison.SosisonEntryType;

@AllArgsConstructor
public enum JsonElementType {
	BOOLEAN(SosisonEntryType.BOOLEAN),
	OBJECT(SosisonEntryType.BEGIN_OBJECT) {
		@Override
		public boolean isJsonPrimitive() {
			return false;
		}
	},
	LIST(SosisonEntryType.BEGIN_LIST) {
		@Override
		public boolean isJsonPrimitive() {
			return false;
		}
	},
	NUMBER(null),
	STRING(SosisonEntryType.STRING),
	NULL(SosisonEntryType.NULL) {
		@Override
		public boolean isJsonPrimitive() {
			return false;
		}
	};

	@Getter
	private final SosisonEntryType beginEntryType;

	public boolean isJsonPrimitive() {
		return true;
	}
}
