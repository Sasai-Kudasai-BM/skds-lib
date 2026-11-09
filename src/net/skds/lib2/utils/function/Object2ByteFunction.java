package net.skds.lib2.utils.function;

@SuppressWarnings("unused")
@FunctionalInterface
public interface Object2ByteFunction<T> {
	byte get(T o);
}
