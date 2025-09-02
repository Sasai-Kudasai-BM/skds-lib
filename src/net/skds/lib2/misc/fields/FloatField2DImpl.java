package net.skds.lib2.misc.fields;

final class FloatField2DImpl implements FloatField2D {

	private final int width;
	private final int height;
	private final float[] array;

	public FloatField2DImpl(int width, int height) {
		this.width = width;
		this.height = height;
		this.array = new float[width * height];
	}

	public FloatField2DImpl(int width, int height, float[] data) {
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
	public float[] toArray() {
		return array;
	}

	@Override
	public float getValue(int x, int y) {
		return array[Field2D.index(x, y, width)];
	}

	@Override
	public void setValue(float value, int x, int y) {
		array[Field2D.index(x, y, width)] = value;
	}

	@Override
	public void increment(float value, int x, int y) {
		array[Field2D.index(x, y, width)] += value;
	}

	@Override
	public void multiply(float value, int x, int y) {
		array[Field2D.index(x, y, width)] *= value;
	}
}
