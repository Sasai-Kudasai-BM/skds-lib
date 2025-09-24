package net.skds.lib2.utils.function;

@FunctionalInterface
public interface FloatBiConsumer<T> {
	void accept(T o, float l);
}
