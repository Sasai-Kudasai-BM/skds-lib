package net.skds.lib2.utils.collection;

import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.function.Object2FloatFunction;
import net.skds.lib2.utils.linkiges.Obj2FloatPair;

import java.util.Iterator;
import java.util.function.Consumer;

public sealed class WeightedPool<T> implements Iterable<T> {

	private static final WeightedPool<?> EMPTY = new Empty<>();

	final Entry[] entries;

	public WeightedPool(Iterable<T> values, Object2FloatFunction<T> weightGetter) {
		float ws = 0;
		int c = 0;
		for (T val : values) {
			c++;
			float w = weightGetter.get(val);
			if (w <= 0) throw new IllegalArgumentException("Invalid weightGetter: Return values must be positive");
			ws += w;
		}
		if (c == 0) throw new IllegalArgumentException("For empty inputs use WeightedPool.empty()");
		float w = 0;
		@SuppressWarnings("unchecked") Entry[] entries = ArrayUtils.createGenericArray(Entry.class, c);
		int i = 0;
		for (T val : values) {
			w += weightGetter.get(val);
			Entry e = new Entry(val, w / ws);
			entries[i++] = e;
		}
		if (w != ws) throw new IllegalArgumentException("Invalid weightGetter: Return values must be input-consistent");
		this.entries = entries;
	}

	public WeightedPool(Iterable<Obj2FloatPair<T>> values) {
		float ws = 0;
		int c = 0;
		for (Obj2FloatPair<T> val : values) {
			c++;
			float w = val.f();
			if (w <= 0) throw new IllegalArgumentException("Invalid weightGetter: Return values must be positive");
			ws += w;
		}
		if (c == 0) throw new IllegalArgumentException("For empty inputs use WeightedPool.empty()");
		float w = 0;
		@SuppressWarnings("unchecked") Entry[] entries = ArrayUtils.createGenericArray(Entry.class, c);
		int i = 0;
		for (Obj2FloatPair<T> val : values) {
			w += val.f();
			Entry e = new Entry(val.o(), w / ws);
			entries[i++] = e;
		}
		if (w != ws) throw new IllegalArgumentException("Invalid weightGetter: Return values must be input-consistent");
		this.entries = entries;
	}

	@SuppressWarnings("unchecked")
	private WeightedPool() {
		this.entries = ArrayUtils.createGenericArray(Entry.class, 0);
	}

	@SuppressWarnings("unchecked")
	public static <T> WeightedPool<T> empty() {
		return (WeightedPool<T>) EMPTY;
	}

	public T get(float f) {
		int half = entries.length / 2;
		int pos = half;

		while (true) {
			Entry e = entries[pos];
			if (e.weightCeiling < f) {
				half = half / 2;
				if (half == 0) half = 1;
				pos += half;
				if (pos == entries.length) {
					return entries[entries.length - 1].value;
				}
			} else {
				if (pos == 0) {
					return e.value;
				}
				Entry e2 = entries[pos - 1];
				if (e2.weightCeiling < f) {
					return e.value;
				}
				half = half / 2;
				if (half == 0) half = 1;
				pos -= half;
			}
		}
	}

	private class Itr implements Iterator<T> {

		int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < entries.length;
		}

		@Override
		public T next() {
			return entries[pos++].value;
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new Itr();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		for (int i = 0; i < entries.length; i++) {
			action.accept(entries[i].value);
		}
	}

	private class Entry {
		final T value;
		final float weightCeiling;

		private Entry(T value, float weightCeiling) {
			this.value = value;
			this.weightCeiling = weightCeiling;
		}
	}

	private static final class Empty<T> extends WeightedPool<T> {

		@Override
		public T get(float f) {
			return null;
		}
	}
}
