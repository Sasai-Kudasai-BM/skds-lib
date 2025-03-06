package net.skds.lib2.utils;

@SuppressWarnings("unused")
public interface AutoCast {

	@SuppressWarnings("unchecked")
	default <T> T cast() {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	static <T> T cast(Object object) {
		return (T) object;
	}
}
