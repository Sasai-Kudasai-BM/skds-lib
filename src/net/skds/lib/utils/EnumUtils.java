package net.skds.lib.utils;

import net.libmerge.Lib2Merge;

@Lib2Merge
public interface EnumUtils<T extends Enum<T>> {

	@SuppressWarnings("unchecked")
	private Enum<T> asEnum() {
		return (Enum<T>) this;
	}
	@SuppressWarnings("unchecked")
	private T[] enumValues() {
		return (T[])asEnum().getClass().getEnumConstants();
	}

	default boolean lEqual(T other) {
		return asEnum().ordinal() <= other.ordinal();
	}

	default boolean gEqual(T other) {
		return asEnum().ordinal() >= other.ordinal();
	}
	default T nextEnumValue() {
		return ArrayUtils.loop(asEnum().ordinal() + 1, enumValues());
	}

	default T previousEnumValue() {
		return ArrayUtils.loop(asEnum().ordinal() - 1, enumValues());
	}
}
