package net.skds.lib.utils.function;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Object2FloatFunction<T> {
	float get(T o);
}
