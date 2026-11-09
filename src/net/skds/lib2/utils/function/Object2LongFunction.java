package net.skds.lib2.utils.function;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Object2LongFunction<T> {
	long get(T o);
}
