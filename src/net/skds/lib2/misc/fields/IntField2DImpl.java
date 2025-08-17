package net.skds.lib2.misc.fields;

public class IntField2DImpl implements IntField2D {

	private final int width;
	private final int height;
	private final int[] array;

	public IntField2DImpl(int width, int height) {
		this.width = width;
		this.height = height;
		this.array = new int[width * height];
	}

	public IntField2DImpl(int width, int height, int[] data) {
		if (width * height != data.length)
			throw new IllegalArgumentException("Expected data length of %s but got %s".formatted(width * height, data.length));
		this.width = width;
		this.height = height;
		this.array = data;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int height() {
		return height;
	}

	@Override
	public int[] toArray() {
		return array;
	}

	@Override
	public int getValue(int x, int y) {
		return array[Field2D.index(x, y, width)];
	}

	@Override
	public void setValue(int value, int x, int y) {
		array[Field2D.index(x, y, width)] = value;
	}

	@Override
	public void increment(int value, int x, int y) {
		array[Field2D.index(x, y, width)] += value;
	}

	@Override
	public void multiply(int value, int x, int y) {
		array[Field2D.index(x, y, width)] *= value;
	}
}
