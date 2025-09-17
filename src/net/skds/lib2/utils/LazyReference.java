package net.skds.lib2.utils;

import java.util.function.Supplier;


public sealed class LazyReference<T> {

	protected T value;
	private final Supplier<T> supplier;

	public LazyReference(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public LazyReference(T value) {
		this.value = value;
		this.supplier = null;
	}

	public T get() {
		T v = this.value;
		if (v == null) {
			v = supplier.get();
			this.value = v;
		}
		return v;
	}

	public void reset() {
		value = null;
	}

	public static final class Synchronized<T> extends LazyReference<T> {

		public Synchronized(Supplier<T> supplier) {
			super(supplier);
		}

		@Override
		public T get() {
			T v = this.value;
			if (v == null) synchronized (this) {
				return super.get();
			}
			return v;
		}
	}
}
