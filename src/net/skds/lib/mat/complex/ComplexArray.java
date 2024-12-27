package net.skds.lib.mat.complex;

@Deprecated // TODO
public final class ComplexArray {

	private final float[] re;
	private final float[] im;

	public ComplexArray(int size) {
		this.re = new float[size];
		this.im = new float[size];
	}

	public float getRe(int index) {
		return re[index];
	}

	public float getIm(int index) {
		return im[index];
	}

	public int size() {
		return re.length;
	}
}
