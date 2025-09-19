package net.skds.lib2.utils.function;

@FunctionalInterface
public interface LongBiConsumer<T> {
	void accept(T o, long l);
}
