package net.skds.lib2.utils.function;

@FunctionalInterface
public interface CharBiConsumer<T> {
	void accept(T o, char v);
}
