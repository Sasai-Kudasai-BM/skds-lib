package net.skds.lib2.utils.collection;

import net.skds.lib2.utils.function.IntBiConsumer;

import java.util.Arrays;

public class IntEnumMap<E extends Enum<E>> extends AbstractEnumMap<E> implements Cloneable {

	private final int[] values;

	public IntEnumMap(Class<E> clazz) {
		super(clazz);
		this.values = new int[universeSize()];
	}

	public IntEnumMap(Class<E> clazz, int[] values) {
		super(clazz);
		if (universeSize() != values.length) throw new IllegalArgumentException(
				"Length of values must be equal to universe size (%s) but it is %s"
						.formatted(universeSize(), values.length)
		);
		this.values = values;
	}

	public int get(E key) {
		return values[key.ordinal()];
	}

	public int put(E key, int value) {
		int i = key.ordinal();
		int old = values[i];
		values[i] = value;
		return old;
	}

	public int increment(E key, int value) {
		return values[key.ordinal()] += value;
	}

	public void increment(IntEnumMap<E> map) {
		int[] array = map.values();
		for (int i = 0, len = array.length; i < len; i++) {
			this.values[i] += array[i];
		}
	}

	public float mul(E key, int value) {
		return values[key.ordinal()] *= value;
	}

	public void foreach(IntBiConsumer<E> action) {
		for (int i = 0; i < values.length; i++) {
			action.accept(keyUniverse[i], values[i]);
		}
	}

	public int[] values() {
		return this.values;
	}

	public void clear() {
		for (int i = 0, len = values.length; i < len; i++) {
			values[i] = 0;
		}
	}

	@Override
	public IntEnumMap<E> clone() {
		return new IntEnumMap<>(enumType, values.clone());
	}

	@Override
	public String toString() {
		return "IntEnumMap" + Arrays.toString(values);
	}
}
