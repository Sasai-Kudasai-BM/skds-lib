package net.skds.lib2.misc.fields;

public class FloatField2DImpl implements FloatField2D {

	private final int width;
	private final int height;
	private final float[] array;

	public FloatField2DImpl(int width, int height) {
		this.width = width;
		this.height = height;
		this.array = new float[width * height];
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
	public float getValue(int x, int y) {
		return array[Field2D.index(x, y, width)];
	}

	@Override
	public void setValue(float value, int x, int y) {
		array[Field2D.index(x, y, width)] = value;
	}
}
