package net.skds.lib2.utils;

import java.util.function.Function;


public sealed class LazyFunctionReference<I, T> {

	protected T value;
	private final Function<I, T> function;

	public LazyFunctionReference(Function<I, T> function) {
		this.function = function;
	}

	public LazyFunctionReference(T value) {
		this.value = value;
		this.function = null;
	}

	public T get(I in) {
		T v = this.value;
		if (v == null) {
			v = function.apply(in);
			this.value = v;
		}
		return v;
	}

	public static final class Synchronized<I, T> extends LazyFunctionReference<I, T> {

		public Synchronized(Function<I, T> function) {
			super(function);
		}

		@Override
		public T get(I in) {
			T v = this.value;
			if (v == null) synchronized (this) {
				return super.get(in);
			}
			return v;
		}
	}
}
