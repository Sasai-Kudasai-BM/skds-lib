package net.skds.lib2.utils.function;

@FunctionalInterface
public interface ByteBiConsumer<T> {
	void accept(T o, byte v);
}
