package net.skds.lib2.utils.collection;

import net.skds.lib2.utils.function.LongBiConsumer;

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

	public void foreach(LongBiConsumer<E> action) {
		for (int i = 0; i < values.length; i++) {
			action.accept(keyUniverse[i], values[i]);
		}
	}

	public void clear() {
		for (int i = 0; i < values.length; i++) {
			values[i] = 0;
		}
	}

	public LongEnumMap<E> clone() {
		return new LongEnumMap<>(enumType, values.clone());
	}
}
