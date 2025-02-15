package net.w3e.lib.utils.suppliers;

import java.util.function.Supplier;

public class CacheSupplier<T> implements Supplier<T> {

	private final Supplier<T> sup;
	private T value = null;

	public CacheSupplier(Supplier<T> sup) {
		this.sup = sup;
	}

	@Override
	public final T get() {
		if (this.value == null) {
			this.value = sup.get();
		}
		return this.value;
	}
}
