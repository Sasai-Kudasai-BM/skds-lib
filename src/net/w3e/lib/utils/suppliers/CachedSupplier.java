package net.w3e.lib.utils.suppliers;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

public interface CachedSupplier<T> extends Supplier<T> {

	void resetCache();

	static <T> CachedSupplier<T> of(Supplier<T> sup) {
		return new CachedSupplierImpl<>(sup);
	}

	@RequiredArgsConstructor
	class CachedSupplierImpl<T> implements CachedSupplier<T> {
		private final Supplier<T> sup;
		private boolean initialized = false;
		private T value = null;

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
			}
			return this.value;
		}
	}

	static <T> CachedSupplier<T> ofValue(T value) {
		return new NonCachedSupplierImpl<>(value);
	}

	record NonCachedSupplierImpl<T>(T value) implements CachedSupplier<T> {
		@Override
		public void resetCache() {
		}

		@Override
		public final T get() {
			return this.value;
		}
	}
}
