package net.skds.lib2.io.json;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum JsonEntryType {
	BEGIN_OBJECT(true),
	END_OBJECT(true),
	BEGIN_LIST(false),
	END_LIST(true),
	STRING(true),
	NUMBER(true),
	BOOLEAN(false),
	NULL(false);

	public final boolean canNameFollow;
}
