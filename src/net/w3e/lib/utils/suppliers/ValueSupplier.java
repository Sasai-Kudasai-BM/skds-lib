package net.w3e.lib.utils.suppliers;

import java.util.function.Supplier;

import net.sdteam.libmerge.Lib1Merge;

@Lib1Merge
public class ValueSupplier<T> implements Supplier<T> {

	private final T value;

	public ValueSupplier(T value) {
		this.value = value;
	}

	@Override
	public final T get() {
		return this.value;
	}
}
