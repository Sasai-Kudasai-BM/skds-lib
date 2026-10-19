package net.skds.lib2.io.codec;

public enum CodecRole {
	SERIALIZE(true, false),
	DESERIALIZE(false, true),
	BOTH(true, true),
	NONE(false, false);

	private final boolean canSerialize;
	private final boolean canDeserialize;

	private CodecRole(final boolean canSerialize, final boolean canDeserialize) {
		this.canSerialize = canSerialize;
		this.canDeserialize = canDeserialize;
	}

	public boolean isCanSerialize() {
		return this.canSerialize;
	}

	public boolean isCanDeserialize() {
		return this.canDeserialize;
	}
}
