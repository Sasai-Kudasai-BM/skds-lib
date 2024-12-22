package net.skds.lib2.io.json;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum JsonEntryType {
	BEGIN_OBJECT,
	END_OBJECT,
	BEGIN_ARRAY,
	END_ARRAY,
	STRING,
	NUMBER,
	BOOLEAN,
	NULL;

}
