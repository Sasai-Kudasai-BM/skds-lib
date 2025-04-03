package net.skds.lib2.mat.matrix2;

public record Matrix2D(
		double m00, double m01,
		double m10, double m11
) implements Matrix2 {

	public static final Matrix2D SINGLE = new Matrix2D(1, 0, 0, 1);

	@Override
	public float m00f() {
		return (float) m00;
	}

	@Override
	public float m01f() {
		return (float) m01;
	}

	@Override
	public float m10f() {
		return (float) m10;
	}

	@Override
	public float m11f() {
		return (float) m11;
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
		int i = this.m00 != 0.0F ? Double.hashCode(this.m00) : 0;
		i = 31 * i + (this.m01 != 0.0F ? Double.hashCode(this.m01) : 0);
		i = 31 * i + (this.m10 != 0.0F ? Double.hashCode(this.m10) : 0);
		return 31 * i + (this.m11 != 0.0F ? Double.hashCode(this.m11) : 0);
	}
}