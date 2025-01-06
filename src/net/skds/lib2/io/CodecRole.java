package net.skds.lib2.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CodecRole {
	SERIALIZE(true, false),
	DESERIALIZE(false, true),
	BOTH(true, true);

	private final boolean canSerialize;
	private final boolean canDeserialize;
}
