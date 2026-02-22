package net.skds.lib2.io.codec.typed;

public interface TypedConfig<T> {
	T getConfigType();

	default void setConfigType(T type) {
	}
}
