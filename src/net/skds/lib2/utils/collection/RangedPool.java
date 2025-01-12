package net.skds.lib2.utils.collection;

import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.linkiges.Obj2FloatPair;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public sealed class RangedPool<T> implements Iterable<Obj2FloatPair<T>> {

	private static final RangedPool<?> EMPTY = new Empty<>();

	private final Entry[] entries;

	public <W extends Obj2FloatPair<T>> RangedPool(List<W> values) {
		float lr = -Float.MAX_VALUE;
		@SuppressWarnings("unchecked") final Entry[] entries = ArrayUtils.createGenericArray(Entry.class, values.size());
		for (int i = 0; i < entries.length; i++) {
			Obj2FloatPair<T> val = values.get(i);
			float ceiling = val.floatValue();
			if (ceiling <= lr) throw new IllegalArgumentException("Invalid ranges");
			lr = ceiling;
			Entry e = new Entry(val.objectValue(), ceiling);
			entries[i] = e;
		}
		this.entries = entries;
	}

	@SuppressWarnings("unchecked")
	private RangedPool() {
		this.entries = ArrayUtils.createGenericArray(Entry.class, 0);
	}

	@SuppressWarnings("unchecked")
	public static <T> RangedPool<T> empty() {
		return (RangedPool<T>) EMPTY;
	}


	public T get(float f) {
		if (entries.length == 0) return null;
		int half = entries.length / 2;
		int pos = half;
		while (true) {
			Entry e = entries[pos];
			if (e.ceiling < f) {
				half = half / 2;
				if (half == 0) half = 1;
				pos += half;
				if (pos == entries.length) {
					return entries[pos - 1].value;
				}
			} else {
				if (pos == 0) {
					return e.value;
				}
				Entry e2 = entries[pos - 1];
				if (e2.ceiling < f) {
					return e.value;
				}
				half = half / 2;
				if (half == 0) half = 1;
				pos -= half;
			}
		}
	}

	private class Itr implements Iterator<Obj2FloatPair<T>> {

		int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < entries.length;
		}

		@Override
		public Obj2FloatPair<T> next() {
			return entries[pos++];
		}
	}

	@Override
	public Iterator<Obj2FloatPair<T>> iterator() {
		return new Itr();
	}

	@Override
	public void forEach(Consumer<? super Obj2FloatPair<T>> action) {
		for (int i = 0; i < entries.length; i++) {
			action.accept(entries[i]);
		}
	}

	private class Entry implements Obj2FloatPair<T> {
		final T value;
		final float ceiling;

		private Entry(T value, float ceiling) {
			this.value = value;
			this.ceiling = ceiling;
		}

		@Override
		public T objectValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return ceiling;
		}
	}

	private static final class Empty<T> extends RangedPool<T> {
		@Override
		public T get(float f) {
			return null;
		}
	}
}
