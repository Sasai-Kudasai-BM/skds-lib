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
		private Supplier<T> sup;
		private boolean initialized = false;
		private T value = null;

		public CacheSupplierImpl(Supplier<T> sup) {
			this.sup = sup;
		}

		@Override
		public void resetCache() {
			this.value = null;
			this.initialized = false;
		}

		@Override
		public final T get() {
			if (!this.initialized) {
				this.initialized = true;
				this.value = sup.get();
				this.sup = null;
			}
			return this.value;
		}
	}
}
