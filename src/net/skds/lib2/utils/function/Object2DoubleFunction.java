package net.skds.lib2.utils.function;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Object2DoubleFunction<T> {
	double get(T o);
}
