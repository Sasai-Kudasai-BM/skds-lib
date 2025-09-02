package net.skds.lib2.misc.fields;

final class FloatField3DImpl implements FloatField3D {

	private final int width;
	private final int height;
	private final int depth;
	private final float[] array;

	public FloatField3DImpl(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.array = new float[width * height * depth];
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
	public int depth() {
		return depth;
	}

	@Override
	public float getValue(int x, int y, int z) {
		return array[Field3D.index(x, y, z, width, height)];
	}

	@Override
	public void setValue(float value, int x, int y, int z) {
		array[Field3D.index(x, y, z, width, height)] = value;
	}
}
