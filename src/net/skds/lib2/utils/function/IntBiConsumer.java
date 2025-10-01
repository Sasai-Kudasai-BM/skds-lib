package net.skds.lib2.utils.function;

@FunctionalInterface
public interface IntBiConsumer<T> {
	void accept(T o, int l);
}
