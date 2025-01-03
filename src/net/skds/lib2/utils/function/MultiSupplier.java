package net.skds.lib2.utils.function;

@FunctionalInterface
public interface MultiSupplier<T> {
	
	T get(Object... args);
}
