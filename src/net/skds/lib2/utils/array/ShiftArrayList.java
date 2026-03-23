package net.skds.lib2.utils.array;

import net.skds.lib2.utils.ArrayUtils;

import java.util.Arrays;
import java.util.Objects;

public class ShiftArrayList<T> implements Cloneable {

	private final T[] array;
	private int pos = 0;

	public ShiftArrayList(Class<T> type, int size) {
		this.array = ArrayUtils.createGenericArray(type, size);
	}

	private ShiftArrayList(ShiftArrayList<T> other) {
		this.array = other.array.clone();
		this.pos = other.pos;
	}

	public T get(int index) {
		Objects.checkIndex(index, this.array.length);
		return array[ArrayUtils.loop(this.pos - index, this.array.length)];
	}

	public void add(T element) {
		int p = ArrayUtils.loop(this.pos + 1, this.array.length);
		this.pos = p;
		this.array[p] = element;
	}

	public int capacity() {
		return this.array.length;
	}

	public void clear() {
		Arrays.fill(this.array, null);
	}

	@Override
	public ShiftArrayList<T> clone() {
		return new ShiftArrayList<>(this);
	}
}
