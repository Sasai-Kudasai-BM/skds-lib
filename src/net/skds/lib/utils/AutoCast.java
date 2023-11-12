package net.skds.lib.utils;

public interface AutoCast {

	@SuppressWarnings("unchecked")
	default <T> T cast() {
		return (T) this;
	}
}
