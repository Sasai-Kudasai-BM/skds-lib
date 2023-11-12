package net.skds.lib.utils;

@FunctionalInterface
public interface FiniteTickable {
	boolean tick();

	default boolean willRemove() {
		return !tick();
	}
}
