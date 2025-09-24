package net.skds.lib2.utils.collection;

import net.skds.lib2.utils.function.FloatBiConsumer;

import java.util.Arrays;

public class FloatEnumMap<E extends Enum<E>> extends AbstractEnumMap<E> implements Cloneable {

	private final float[] values;

	public FloatEnumMap(Class<E> clazz) {
		super(clazz);
		this.values = new float[universeSize()];
	}

	public FloatEnumMap(Class<E> clazz, float[] values) {
		super(clazz);
		if (universeSize() != values.length) throw new IllegalArgumentException(
				"Length of values must be equal to universe size (%s) but it is %s"
						.formatted(universeSize(), values.length)
		);
		this.values = values;
	}

	public float get(E key) {
		return values[key.ordinal()];
	}

	public float put(E key, float value) {
		int i = key.ordinal();
		float old = values[i];
		values[i] = value;
		return old;
	}

	public void foreach(FloatBiConsumer<E> action) {
		for (int i = 0; i < values.length; i++) {
			action.accept(keyUniverse[i], values[i]);
		}
	}

	public float[] values() {
		return this.values;
	}

	public void clear() {
		for (int i = 0, len = values.length; i < len; i++) {
			values[i] = 0;
		}
	}

	@Override
	public FloatEnumMap<E> clone() {
		return new FloatEnumMap<>(enumType, values.clone());
	}

	@Override
	public String toString() {
		return "FloatEnumMap" + Arrays.toString(values);
	}
}
