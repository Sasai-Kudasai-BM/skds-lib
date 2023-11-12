package net.skds.lib.mat.graphics;

import net.skds.lib.mat.Matrix3;

public class Matrix3f {

	public static final Matrix3f SINGLE = new Matrix3f();

	public float m00 = 1f;
	public float m01 = 0f;
	public float m02 = 0f;
	public float m10 = 0f;
	public float m11 = 1f;
	public float m12 = 0f;
	public float m20 = 0f;
	public float m21 = 0f;
	public float m22 = 1f;

	public Matrix3f() {
	}

	public Matrix3f(Matrix3 m3) {
		this.m00 = (float) m3.m00;
		this.m01 = (float) m3.m01;
		this.m02 = (float) m3.m02;
		this.m10 = (float) m3.m10;
		this.m11 = (float) m3.m11;
		this.m12 = (float) m3.m12;
		this.m20 = (float) m3.m20;
		this.m21 = (float) m3.m21;
		this.m22 = (float) m3.m22;
	}

	public Matrix3f(Vec3f[] normals) {
		m00 = normals[0].x;
		m01 = normals[1].x;
		m02 = normals[2].x;
		m10 = normals[0].y;
		m11 = normals[1].y;
		m12 = normals[2].y;
		m20 = normals[0].z;
		m21 = normals[1].z;
		m22 = normals[2].z;
	}

	public Matrix3f(Quatf quaternionIn) {
		float f = quaternionIn.x;
		float f1 = quaternionIn.y;
		float f2 = quaternionIn.z;
		float f3 = quaternionIn.w;
		float f4 = 2.0f * f * f;
		float f5 = 2.0f * f1 * f1;
		float f6 = 2.0f * f2 * f2;
		this.m00 = 1.0f - f5 - f6;
		this.m11 = 1.0f - f6 - f4;
		this.m22 = 1.0f - f4 - f5;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0f * (f7 + f12);
		this.m01 = 2.0f * (f7 - f12);
		this.m20 = 2.0f * (f9 - f11);
		this.m02 = 2.0f * (f9 + f11);
		this.m21 = 2.0f * (f8 + f10);
		this.m12 = 2.0f * (f8 - f10);
	}

	public Matrix3f set(Quatf quaternionIn) {
		float f = quaternionIn.x;
		float f1 = quaternionIn.y;
		float f2 = quaternionIn.z;
		float f3 = quaternionIn.w;
		float f4 = 2.0f * f * f;
		float f5 = 2.0f * f1 * f1;
		float f6 = 2.0f * f2 * f2;
		this.m00 = 1.0f - f5 - f6;
		this.m11 = 1.0f - f6 - f4;
		this.m22 = 1.0f - f4 - f5;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0f * (f7 + f12);
		this.m01 = 2.0f * (f7 - f12);
		this.m20 = 2.0f * (f9 - f11);
		this.m02 = 2.0f * (f9 + f11);
		this.m21 = 2.0f * (f8 + f10);
		this.m12 = 2.0f * (f8 - f10);
		return this;
	}

	public Vec3f left() {
		return new Vec3f(m00, m10, m20);
	}

	public Vec3f up() {
		return new Vec3f(m01, m11, m21);
	}

	public Vec3f forward() {
		return new Vec3f(m02, m12, m22);
	}

	public Matrix4f to4f() {
		return new Matrix4f(this);
	}

	public Matrix3f transpose() {
		float f = this.m01;
		this.m01 = this.m10;
		this.m10 = f;
		f = this.m02;
		this.m02 = this.m20;
		this.m20 = f;
		f = this.m12;
		this.m12 = this.m21;
		this.m21 = f;
		return this;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
			Matrix3f Matrix3d = (Matrix3f) p_equals_1_;
			return Float.compare(Matrix3d.m00, this.m00) == 0 && Float.compare(Matrix3d.m01, this.m01) == 0
					&& Float.compare(Matrix3d.m02, this.m02) == 0 && Float.compare(Matrix3d.m10, this.m10) == 0
					&& Float.compare(Matrix3d.m11, this.m11) == 0 && Float.compare(Matrix3d.m12, this.m12) == 0
					&& Float.compare(Matrix3d.m20, this.m20) == 0 && Float.compare(Matrix3d.m21, this.m21) == 0
					&& Float.compare(Matrix3d.m22, this.m22) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.m00 != 0.0F ? Float.floatToIntBits((float) this.m00) : 0;
		i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits((float) this.m01) : 0);
		i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits((float) this.m02) : 0);
		i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits((float) this.m10) : 0);
		i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits((float) this.m11) : 0);
		i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits((float) this.m12) : 0);
		i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits((float) this.m20) : 0);
		i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits((float) this.m21) : 0);
		return 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits((float) this.m22) : 0);
	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("Matrix3:\n");
		stringbuilder.append(this.m00);
		stringbuilder.append(" ");
		stringbuilder.append(this.m01);
		stringbuilder.append(" ");
		stringbuilder.append(this.m02);
		stringbuilder.append("\n");
		stringbuilder.append(this.m10);
		stringbuilder.append(" ");
		stringbuilder.append(this.m11);
		stringbuilder.append(" ");
		stringbuilder.append(this.m12);
		stringbuilder.append("\n");
		stringbuilder.append(this.m20);
		stringbuilder.append(" ");
		stringbuilder.append(this.m21);
		stringbuilder.append(" ");
		stringbuilder.append(this.m22);
		stringbuilder.append("\n");
		return stringbuilder.toString();
	}

	public void setIdentity() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
	}

	public float adjugateAndDet() {
		float f = this.m11 * this.m22 - this.m12 * this.m21;
		float f1 = -(this.m10 * this.m22 - this.m12 * this.m20);
		float f2 = this.m10 * this.m21 - this.m11 * this.m20;
		float f3 = -(this.m01 * this.m22 - this.m02 * this.m21);
		float f4 = this.m00 * this.m22 - this.m02 * this.m20;
		float f5 = -(this.m00 * this.m21 - this.m01 * this.m20);
		float f6 = this.m01 * this.m12 - this.m02 * this.m11;
		float f7 = -(this.m00 * this.m12 - this.m02 * this.m10);
		float f8 = this.m00 * this.m11 - this.m01 * this.m10;
		float f9 = this.m00 * f + this.m01 * f1 + this.m02 * f2;
		this.m00 = f;
		this.m10 = f1;
		this.m20 = f2;
		this.m01 = f3;
		this.m11 = f4;
		this.m21 = f5;
		this.m02 = f6;
		this.m12 = f7;
		this.m22 = f8;
		return f9;
	}

	public boolean invert() {
		float f = this.adjugateAndDet();
		if (Math.abs(f) > 1.0E-6F) {
			this.mul(f);
			return true;
		} else {
			return false;
		}
	}

	public Matrix3f mul(Matrix3f m2) {
		float f = this.m00 * m2.m00 + this.m01 * m2.m10 + this.m02 * m2.m20;
		float f1 = this.m00 * m2.m01 + this.m01 * m2.m11 + this.m02 * m2.m21;
		float f2 = this.m00 * m2.m02 + this.m01 * m2.m12 + this.m02 * m2.m22;
		float f3 = this.m10 * m2.m00 + this.m11 * m2.m10 + this.m12 * m2.m20;
		float f4 = this.m10 * m2.m01 + this.m11 * m2.m11 + this.m12 * m2.m21;
		float f5 = this.m10 * m2.m02 + this.m11 * m2.m12 + this.m12 * m2.m22;
		float f6 = this.m20 * m2.m00 + this.m21 * m2.m10 + this.m22 * m2.m20;
		float f7 = this.m20 * m2.m01 + this.m21 * m2.m11 + this.m22 * m2.m21;
		float f8 = this.m20 * m2.m02 + this.m21 * m2.m12 + this.m22 * m2.m22;
		this.m00 = f;
		this.m01 = f1;
		this.m02 = f2;
		this.m10 = f3;
		this.m11 = f4;
		this.m12 = f5;
		this.m20 = f6;
		this.m21 = f7;
		this.m22 = f8;
		return this;
	}

	public Matrix3f mul(Quatf q) {
		return this.mul(new Matrix3f(q));
	}

	public void mul(float scale) {
		this.m00 *= scale;
		this.m01 *= scale;
		this.m02 *= scale;
		this.m10 *= scale;
		this.m11 *= scale;
		this.m12 *= scale;
		this.m20 *= scale;
		this.m21 *= scale;
		this.m22 *= scale;
	}

	//https://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToEuler/index.htm
	public final Vec3f getYPRAngles() {
		float yaw = 0;
		float pitch = 0;
		float roll = 0;
		// Assuming the angles are in radians.
		if (m10 > 0.999999) { // singularity at north pole
			yaw = (float) Math.atan2(m02, m22);
			pitch = (float) Math.PI / 2;
			roll = 0;
		} else if (m10 < -0.999999) { // singularity at south pole
			yaw = (float) Math.atan2(m02, m22);
			pitch = (float) -Math.PI / 2;
			roll = 0;
		} else {
			yaw = (float) Math.atan2(-m20, m00);
			pitch = (float) Math.atan2(-m12, m11);
			roll = (float) Math.asin(m10);
		}

		return new Vec3f(yaw, pitch, roll);
	}
	/*
	
		float f = quaternionIn.x;
		float f1 = quaternionIn.y;
		float f2 = quaternionIn.z;
		float f3 = quaternionIn.w;
		float f4 = 2.0 * f * f;
		float f5 = 2.0 * f1 * f1;
		float f6 = 2.0 * f2 * f2;
		this.m00 = 1.0 - f5 - f6;
		this.m11 = 1.0 - f6 - f4;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0 * (f7 + f12);
		this.m20 = 2.0 * (f9 - f11);
		this.m12 = 2.0 * (f8 - f10);
	*/

	// =====================================

	public Matrix3f copy() {
		Matrix3f m3 = new Matrix3f();
		m3.m00 = this.m00;
		m3.m01 = this.m01;
		m3.m02 = this.m02;
		m3.m10 = this.m10;
		m3.m11 = this.m11;
		m3.m12 = this.m12;
		m3.m20 = this.m20;
		m3.m21 = this.m21;
		m3.m22 = this.m22;
		return m3;
	}

	public Vec3f[] asNormals() {
		Vec3f[] norms = new Vec3f[3];
		norms[0] = new Vec3f(m00, m10, m20);
		norms[1] = new Vec3f(m01, m11, m21);
		norms[2] = new Vec3f(m02, m12, m22);

		return norms;
	}

	public Vec3f getZYXAnglesOld(boolean degrees) {

		float m20sq = (float) Math.sqrt(1 - (m20 * m20));

		float a = (float) Math.atan2(m10, m00);
		float b = (float) Math.atan2(m20, m20sq);
		float g = (float) Math.atan2(m21, m22);

		Vec3f vec3 = new Vec3f(g, b, a);

		//log.info(m10 / m00);

		return degrees ? vec3.scale(180f / (float) Math.PI) : vec3;
	}

	public Vec3f getZYXAngles(boolean degrees) {

		float y = (float) Math.asin(-m20);

		float x = (float) Math.atan2(m21, m22);
		float z = (float) Math.atan2(m10, m00);

		Vec3f vec3 = new Vec3f(x, -y, -z);

		if (degrees) {
			vec3.scale(180f / (float) Math.PI);
		}

		//log.info(vec3);

		return vec3;
	}

}