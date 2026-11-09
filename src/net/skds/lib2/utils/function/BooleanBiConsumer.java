package net.skds.lib2.utils.function;

@FunctionalInterface
public interface BooleanBiConsumer<T> {
	void accept(T o, boolean v);
}
