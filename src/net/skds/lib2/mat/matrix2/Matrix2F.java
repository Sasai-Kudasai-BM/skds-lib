package net.skds.lib2.mat.matrix2;

public record Matrix2F(
		float m00f, float m01f,
		float m10f, float m11f
) implements Matrix2 {


	public static final Matrix2F SINGLE = new Matrix2F(1, 0, 0, 1);

	@Override
	public double m00() {
		return m00f;
	}

	@Override
	public double m01() {
		return m01f;
	}

	@Override
	public double m10() {
		return m10f;
	}

	@Override
	public double m11() {
		return m11f;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Matrix2 m) {
			return Matrix2.equals(this, m);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int i = this.m00f != 0.0F ? Float.floatToIntBits(this.m00f) : 0;
		i = 31 * i + (this.m01f != 0.0F ? Float.floatToIntBits(this.m01f) : 0);
		i = 31 * i + (this.m10f != 0.0F ? Float.floatToIntBits(this.m10f) : 0);
		return 31 * i + (this.m11f != 0.0F ? Float.floatToIntBits(this.m11f) : 0);
	}
}