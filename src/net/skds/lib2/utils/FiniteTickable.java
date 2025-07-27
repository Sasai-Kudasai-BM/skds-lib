package net.skds.lib2.utils;

@SuppressWarnings("unused")
@FunctionalInterface
public interface FiniteTickable {

	/**
	 * false - stop <br>
	 * true - continue
	 */
	boolean tick();

	default boolean willRemove() {
		return !tick();
	}
}
