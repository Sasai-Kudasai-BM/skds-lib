package net.skds.lib2.utils.function;

@FunctionalInterface
public interface DoubleBiConsumer<T> {
	void accept(T o, double v);
}
