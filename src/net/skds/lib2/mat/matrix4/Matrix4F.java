package net.skds.lib2.mat.matrix4;

public record Matrix4F(
		float m00f, float m01f, float m02f, float m03f,
		float m10f, float m11f, float m12f, float m13f,
		float m20f, float m21f, float m22f, float m23f,
		float m30f, float m31f, float m32f, float m33f
) implements Matrix4 {

	public static final Matrix4F SINGLE = new Matrix4F(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);

	@Override
	public double m00() {
		return this.m00f;
	}

	@Override
	public double m01() {
		return this.m01f;
	}

	@Override
	public double m02() {
		return this.m02f;
	}

	@Override
	public double m03() {
		return this.m03f;
	}

	@Override
	public double m10() {
		return this.m10f;
	}

	@Override
	public double m11() {
		return this.m11f;
	}

	@Override
	public double m12() {
		return this.m12f;
	}

	@Override
	public double m13() {
		return this.m13f;
	}

	@Override
	public double m20() {
		return this.m20f;
	}

	@Override
	public double m21() {
		return this.m21f;
	}

	@Override
	public double m22() {
		return this.m22f;
	}

	@Override
	public double m23() {
		return this.m23f;
	}

	@Override
	public double m30() {
		return this.m30f;
	}

	@Override
	public double m31() {
		return this.m31f;
	}

	@Override
	public double m32() {
		return this.m32f;
	}

	@Override
	public double m33() {
		return this.m33f;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Matrix4 m) {
			return Matrix4.equals(this, m);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int i = this.m00f != 0.0F ? Float.floatToIntBits(this.m00f) : 0;
		i = 31 * i + (this.m01f != 0.0F ? Float.floatToIntBits(this.m01f) : 0);
		i = 31 * i + (this.m02f != 0.0F ? Float.floatToIntBits(this.m02f) : 0);
		i = 31 * i + (this.m03f != 0.0F ? Float.floatToIntBits(this.m03f) : 0);

		i = 31 * i + (this.m10f != 0.0F ? Float.floatToIntBits(this.m10f) : 0);
		i = 31 * i + (this.m11f != 0.0F ? Float.floatToIntBits(this.m11f) : 0);
		i = 31 * i + (this.m12f != 0.0F ? Float.floatToIntBits(this.m12f) : 0);
		i = 31 * i + (this.m13f != 0.0F ? Float.floatToIntBits(this.m13f) : 0);

		i = 31 * i + (this.m20f != 0.0F ? Float.floatToIntBits(this.m20f) : 0);
		i = 31 * i + (this.m21f != 0.0F ? Float.floatToIntBits(this.m21f) : 0);
		i = 31 * i + (this.m22f != 0.0F ? Float.floatToIntBits(this.m22f) : 0);
		i = 31 * i + (this.m23f != 0.0F ? Float.floatToIntBits(this.m23f) : 0);

		i = 31 * i + (this.m30f != 0.0F ? Float.floatToIntBits(this.m30f) : 0);
		i = 31 * i + (this.m31f != 0.0F ? Float.floatToIntBits(this.m31f) : 0);
		i = 31 * i + (this.m32f != 0.0F ? Float.floatToIntBits(this.m32f) : 0);
		return 31 * i + (this.m33f != 0.0F ? Float.floatToIntBits(this.m33f) : 0);
	}
}
