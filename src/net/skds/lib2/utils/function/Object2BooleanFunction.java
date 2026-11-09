package net.skds.lib2.utils.function;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Object2BooleanFunction<T> {
	boolean get(T o);
}
