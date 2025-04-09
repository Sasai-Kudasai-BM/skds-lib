package net.w3e.lib.utils.suppliers;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

public interface CacheSupplier<T> extends Supplier<T> {

	void resetCache();

	static <T> CacheSupplier<T> of(Supplier<T> sup) {
		return new CacheSupplierImpl<>(sup);
	}

	@RequiredArgsConstructor
	class CacheSupplierImpl<T> implements CacheSupplier<T> {
		private final Supplier<T> sup;
		private T value = null;

		@Override
		public void resetCache() {
			this.value = null;
		}

		@Override
		public final T get() {
			if (this.value == null) {
				this.value = sup.get();
			}
			return this.value;
		}
	}
}
