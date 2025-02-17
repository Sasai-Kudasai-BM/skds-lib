package net.skds.lib2.mat.complex;

public record ComplexFloatArray(float[] array) implements Cloneable {

	public ComplexFloatArray(int size) {
		this(new float[size * 2]);
	}

	public int length() {
		return array.length / 2;
	}

	private void checkLength(ComplexFloatArray other) {
		if (other.array.length != this.array.length)
			throw new IllegalArgumentException("ComplexFloatArray's must be equal size");
	}

	public ComplexFloat get(int index) {
		int p = index * 2;
		return new ComplexFloat(p, p + 1);
	}

	public float getRe(int index) {
		return array[index * 2];
	}

	public float getIm(int index) {
		return array[index * 2 + 1];
	}

	public void add(int dstPos, float re, float im) {
		int p = dstPos * 2;
		array[p] += re;
		array[p + 1] += im;
	}

	public void add(int dstPos, ComplexFloat value) {
		int p = dstPos * 2;
		array[p] += value.re();
		array[p + 1] += value.im();
	}

	public void add(int myPos, int otherPos, ComplexFloatArray other) {
		int p = myPos * 2;
		int p2 = otherPos * 2;
		array[p] += other.array[p2];
		array[p + 1] += other.array[p2 + 1];
	}

	public void add(ComplexFloatArray other) {
		checkLength(other);
		int l = array.length;
		for (int i = 0; i < l; i++) {
			array[i] += other.array[i];
		}
	}

	public void mul(int dstPos, float re, float im) {
		int p = dstPos * 2;
		float r = array[p];
		float i = array[p + 1];
		array[p] = r * re - i * im;
		array[p + 1] = r * im + i * re;
	}

	public void mul(int dstPos, ComplexFloat other) {
		int p = dstPos * 2;
		float re = other.re();
		float im = other.im();
		float r = array[p];
		float i = array[p + 1];
		array[p] = r * re - i * im;
		array[p + 1] = r * im + i * re;
	}

	public void mul(int myPos, int otherPos, ComplexFloatArray other) {
		int p = myPos * 2;
		int p2 = otherPos * 2;
		float re = other.array[p2];
		float im = other.array[p2 + 1];
		float r = array[p];
		float i = array[p + 1];
		array[p] = r * re - i * im;
		array[p + 1] = r * im + i * re;
	}

	public void mul(ComplexFloatArray other) {
		checkLength(other);
		int p = array.length / 2;
		for (int j = 0; j < p; j++) {
			float re = other.array[p];
			float im = other.array[p + 1];
			float r = array[p];
			float i = array[p + 1];
			array[p] = r * re - i * im;
			array[p + 1] = r * im + i * re;
		}
	}

	public void sub(int dstPos, float re, float im) {
		int p = dstPos * 2;
		array[p] -= re;
		array[p + 1] -= im;
	}

	public void sub(int dstPos, ComplexFloat value) {
		int p = dstPos * 2;
		array[p] -= value.re();
		array[p + 1] -= value.im();
	}

	public void sub(int myPos, int otherPos, ComplexFloatArray other) {
		int p = myPos * 2;
		int p2 = otherPos * 2;
		array[p] -= other.array[p2];
		array[p + 1] -= other.array[p2 + 1];
	}

	public void sub(ComplexFloatArray other) {
		checkLength(other);
		int l = array.length;
		for (int i = 0; i < l; i++) {
			array[i] -= other.array[i];
		}
	}

	public void inverse(int dstPos) {
		int p = dstPos * 2;
		array[p] *= -1;
		array[p + 1] *= -1;
	}

	public void conjugate(int dstPos) {
		int p = dstPos * 2;
		array[p + 1] *= -1;
	}

	public void inverse() {
		int l = array.length;
		for (int i = 0; i < l; i++) {
			array[i] -= array[i];
		}
	}

	public void conjugate() {
		int p = array.length / 2;
		for (int j = 0; j < p; j++) {
			array[p + 1] -= array[p + 1];
		}
	}

	@Override
	protected ComplexFloatArray clone() {
		return new ComplexFloatArray(this.array.clone());
	}
}
