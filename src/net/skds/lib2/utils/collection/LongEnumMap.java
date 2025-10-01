package net.skds.lib2.utils.collection;

import net.skds.lib2.utils.function.LongBiConsumer;

import java.util.Arrays;

public class LongEnumMap<E extends Enum<E>> extends AbstractEnumMap<E> implements Cloneable {

	private final long[] values;

	public LongEnumMap(Class<E> clazz) {
		super(clazz);
		this.values = new long[universeSize()];
	}

	public LongEnumMap(Class<E> clazz, long[] values) {
		super(clazz);
		if (universeSize() != values.length) throw new IllegalArgumentException(
				"Length of values must be equal to universe size (%s) but it is %s"
						.formatted(universeSize(), values.length)
		);
		this.values = values;
	}

	public long get(E key) {
		return values[key.ordinal()];
	}

	public long put(E key, long value) {
		int i = key.ordinal();
		long old = values[i];
		values[i] = value;
		return old;
	}

	public long increment(E key, long value) {
		return values[key.ordinal()] += value;
	}

	public void increment(LongEnumMap<E> map) {
		long[] array = map.values();
		for (int i = 0, len = array.length; i < len; i++) {
			this.values[i] += array[i];
		}
	}

	public long mul(E key, long value) {
		return values[key.ordinal()] *= value;
	}

	public void foreach(LongBiConsumer<E> action) {
		for (int i = 0; i < values.length; i++) {
			action.accept(keyUniverse[i], values[i]);
		}
	}

	public long[] values() {
		return this.values;
	}

	public void clear() {
		for (int i = 0, len = values.length; i < len; i++) {
			values[i] = 0;
		}
	}

	@Override
	public LongEnumMap<E> clone() {
		return new LongEnumMap<>(enumType, values.clone());
	}

	@Override
	public String toString() {
		return "LongEnumMap" + Arrays.toString(values);
	}
}
