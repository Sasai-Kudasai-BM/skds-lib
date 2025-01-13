package net.skds.lib.utils.collection;


import net.skds.lib.mat.FastMath;
import net.skds.lib.utils.ArrayUtils;
import net.skds.lib.utils.function.Object2FloatFunction;
import net.skds.lib.utils.linkiges.Obj2FloatPair;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public sealed class WeightedPool<T> implements Iterable<Obj2FloatPair<T>>, Cloneable {

	private static final WeightedPool<?> EMPTY = new Empty<>();

	private Entry[] entries;

	public WeightedPool(Iterable<T> values, Object2FloatFunction<T> weightGetter) {
		float ws = 0;
		int c = 0;
		for (T val : values) {
			c++;
			float w = weightGetter.get(val);
			if (w <= 0) throw new IllegalArgumentException("Invalid weightGetter: Return values must be positive");
			ws += w;
		}
		float w = 0;
		@SuppressWarnings("unchecked") Entry[] entries = ArrayUtils.createGenericArray(Entry.class, c);
		int i = 0;
		for (T val : values) {
			float weight = weightGetter.get(val);
			w += weight;
			Entry e = new Entry(val, weight, w / ws, weight / ws);
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
			float w = val.floatValue();
			if (w <= 0) throw new IllegalArgumentException("Invalid weightGetter: Return values must be positive");
			ws += w;
		}
		float w = 0;
		@SuppressWarnings("unchecked") Entry[] entries = ArrayUtils.createGenericArray(Entry.class, c);
		int i = 0;
		for (Obj2FloatPair<T> val : values) {
			float weight = val.floatValue();
			w += weight;
			Entry e = new Entry(val.objectValue(), weight, w / ws, weight / ws);
			entries[i++] = e;
		}
		if (w != ws) throw new IllegalArgumentException("Invalid weightGetter: Return values must be input-consistent");
		this.entries = entries;
	}

	@SuppressWarnings("unchecked")
	private WeightedPool() {
		this.entries = ArrayUtils.createGenericArray(Entry.class, 0);
	}

	private WeightedPool(WeightedPool<T> parent) {
		final var pes = parent.entries;
		final var entries = ArrayUtils.createGenericArray(Entry.class, pes.length);
		for (int i = 0; i < entries.length; i++) {
			entries[i] = pes[i].clone();
		}
		//noinspection unchecked
		this.entries = entries;
	}

	@SuppressWarnings("unchecked")
	public static <T> WeightedPool<T> empty() {
		return (WeightedPool<T>) EMPTY;
	}

	public T getRandom() {
		return get(FastMath.RANDOM.nextFloat());
	}

	public T getAndRemoveRandom() {
		return getAndRemove(FastMath.RANDOM.nextFloat());
	}

	public T get(float f) {
		if (entries.length == 0) return null;
		int half = entries.length / 2;
		int pos = half;
		while (true) {
			Entry e = entries[pos];
			if (e.weightCeiling < f) {
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
				if (e2.weightCeiling < f) {
					return e.value;
				}
				half = half / 2;
				if (half == 0) half = 1;
				pos -= half;
			}
		}
	}

	public T getAndRemove(float f) {
		if (entries.length == 0) return null;
		int half = entries.length / 2;
		int pos = half;
		while (true) {
			Entry e = entries[pos];
			if (e.weightCeiling < f) {
				half = half / 2;
				if (half == 0) half = 1;
				pos += half;
				if (pos == entries.length) {
					T value = entries[pos - 1].value;
					remove(pos - 1);
					return value;
				}
			} else {
				if (pos == 0) {
					remove(0);
					return e.value;
				}
				Entry e2 = entries[pos - 1];
				if (e2.weightCeiling < f) {
					remove(pos);
					return e.value;
				}
				half = half / 2;
				if (half == 0) half = 1;
				pos -= half;
			}
		}
	}

	private void remove(int pos) {
		var ers = entries;
		if (ers.length == 0) return;
		@SuppressWarnings("unchecked")
		Entry[] newEntries = (Entry[]) Array.newInstance(Entry.class, ers.length - 1);
		float ws = 0;
		for (int i = 0; i < pos; i++) {
			Entry e = ers[i];
			ws += e.weight;
		}
		for (int i = pos + 1; i < ers.length; i++) {
			Entry e = ers[i];
			ws += e.weight;
		}
		float ws2 = 0;
		for (int i = 0; i < pos; i++) {
			Entry e = ers[i];
			e.chance = e.weight / ws;
			ws2 += e.chance;
			e.weightCeiling = ws2;
			newEntries[i] = e;
		}
		for (int i = pos + 1; i < ers.length; i++) {
			Entry e = ers[i];
			e.chance = e.weight / ws;
			ws2 += e.chance;
			e.weightCeiling = ws2;
			newEntries[i - 1] = e;
		}
		this.entries = newEntries;
	}

	@Override
	public WeightedPool<T> clone() {
		return new WeightedPool<>(this);
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

		@Override
		public void remove() {
			WeightedPool.this.remove(pos);
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

	private class Entry implements Obj2FloatPair<T>, Cloneable {
		final T value;
		final float weight;
		float weightCeiling;
		float chance;

		private Entry(T value, float weight, float weightCeiling, float chance) {
			this.value = value;
			this.weight = weight;
			this.weightCeiling = weightCeiling;
			this.chance = chance;
		}

		@Override
		public T objectValue() {
			return value;
		}

		@Override
		public float floatValue() {
			return chance;
		}

		@SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
		@Override
		protected Entry clone() {
			return new Entry(value, weight, weightCeiling, chance);
		}
	}

	private static final class Empty<T> extends WeightedPool<T> {

		@Override
		public T get(float f) {
			return null;
		}
	}
}
