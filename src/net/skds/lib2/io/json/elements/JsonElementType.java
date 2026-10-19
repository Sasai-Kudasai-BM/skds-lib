package net.skds.lib2.io.json.elements;

import net.skds.lib2.io.sosison.SosisonEntryType;

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
	private final SosisonEntryType beginEntryType;

	public boolean isJsonPrimitive() {
		return true;
	}

	private JsonElementType(final SosisonEntryType beginEntryType) {
		this.beginEntryType = beginEntryType;
	}

	public SosisonEntryType getBeginEntryType() {
		return this.beginEntryType;
	}
}
