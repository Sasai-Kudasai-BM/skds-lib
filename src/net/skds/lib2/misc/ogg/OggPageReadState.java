package net.skds.lib2.misc.ogg;

public enum OggPageReadState {
	NONE,
	HEADER,
	TABLE,
	FULL;

	public boolean isDone() {
		return this == FULL;
	}
}
