package net.skds.lib2.utils.function;

@FunctionalInterface
public interface ShortBiConsumer<T> {
	void accept(T o, short v);
}
