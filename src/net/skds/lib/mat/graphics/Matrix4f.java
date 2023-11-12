package net.skds.lib.mat.graphics;

import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Quat;
import net.skds.lib.mat.Vec3;

import java.nio.ByteBuffer;

public final class Matrix4f {
	public float m00 = 1.0F;
	public float m01 = 0.0F;
	public float m02 = 0.0F;
	public float m03 = 0.0F;
	public float m10 = 0.0F;
	public float m11 = 1.0F;
	public float m12 = 0.0F;
	public float m13 = 0.0F;
	public float m20 = 0.0F;
	public float m21 = 0.0F;
	public float m22 = 1.0F;
	public float m23 = 0.0F;
	public float m30 = 0.0F;
	public float m31 = 0.0F;
	public float m32 = 0.0F;
	public float m33 = 1.0F;

	public static final int BYTESIZE = 64;

	public Matrix4f() {
	}

	public Matrix4f(Matrix3f matrixIn) {
		this.m00 = matrixIn.m00;
		this.m01 = matrixIn.m01;
		this.m02 = matrixIn.m02;
		this.m03 = 0;
		this.m10 = matrixIn.m10;
		this.m11 = matrixIn.m11;
		this.m12 = matrixIn.m12;
		this.m13 = 0;
		this.m20 = matrixIn.m20;
		this.m21 = matrixIn.m21;
		this.m22 = matrixIn.m22;
		this.m23 = 0;
		this.m30 = 0;
		this.m31 = 0;
		this.m32 = 0;
		this.m33 = 1;
	}

	public Matrix4f(Matrix3 matrixIn) {
		this.m00 = (float) matrixIn.m00;
		this.m01 = (float) matrixIn.m01;
		this.m02 = (float) matrixIn.m02;
		this.m03 = (float) 0;
		this.m10 = (float) matrixIn.m10;
		this.m11 = (float) matrixIn.m11;
		this.m12 = (float) matrixIn.m12;
		this.m13 = (float) 0;
		this.m20 = (float) matrixIn.m20;
		this.m21 = (float) matrixIn.m21;
		this.m22 = (float) matrixIn.m22;
		this.m23 = (float) 0;
		this.m30 = (float) 0;
		this.m31 = (float) 0;
		this.m32 = (float) 0;
		this.m33 = (float) 1;
	}

	public Matrix4f(Matrix4f matrixIn) {
		this.m00 = matrixIn.m00;
		this.m01 = matrixIn.m01;
		this.m02 = matrixIn.m02;
		this.m03 = matrixIn.m03;
		this.m10 = matrixIn.m10;
		this.m11 = matrixIn.m11;
		this.m12 = matrixIn.m12;
		this.m13 = matrixIn.m13;
		this.m20 = matrixIn.m20;
		this.m21 = matrixIn.m21;
		this.m22 = matrixIn.m22;
		this.m23 = matrixIn.m23;
		this.m30 = matrixIn.m30;
		this.m31 = matrixIn.m31;
		this.m32 = matrixIn.m32;
		this.m33 = matrixIn.m33;
	}

	public Matrix4f(Quat quaternionIn) {
		float f = (float) quaternionIn.x;
		float f1 = (float) quaternionIn.y;
		float f2 = (float) quaternionIn.z;
		float f3 = (float) quaternionIn.w;
		float f4 = 2.0F * f * f;
		float f5 = 2.0F * f1 * f1;
		float f6 = 2.0F * f2 * f2;
		this.m00 = 1.0F - f5 - f6;
		this.m11 = 1.0F - f6 - f4;
		this.m22 = 1.0F - f4 - f5;
		this.m33 = 1.0F;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0F * (f7 + f12);
		this.m01 = 2.0F * (f7 - f12);
		this.m20 = 2.0F * (f9 - f11);
		this.m02 = 2.0F * (f9 + f11);
		this.m21 = 2.0F * (f8 + f10);
		this.m12 = 2.0F * (f8 - f10);
	}

	public Matrix4f(Quatf quaternionIn) {
		float f = quaternionIn.x;
		float f1 = quaternionIn.y;
		float f2 = quaternionIn.z;
		float f3 = quaternionIn.w;
		float f4 = 2.0F * f * f;
		float f5 = 2.0F * f1 * f1;
		float f6 = 2.0F * f2 * f2;
		this.m00 = 1.0F - f5 - f6;
		this.m11 = 1.0F - f6 - f4;
		this.m22 = 1.0F - f4 - f5;
		this.m33 = 1.0F;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		this.m10 = 2.0F * (f7 + f12);
		this.m01 = 2.0F * (f7 - f12);
		this.m20 = 2.0F * (f9 - f11);
		this.m02 = 2.0F * (f9 + f11);
		this.m21 = 2.0F * (f8 + f10);
		this.m12 = 2.0F * (f8 - f10);
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
			Matrix4f matrix4f = (Matrix4f) p_equals_1_;
			return Float.compare(matrix4f.m00, this.m00) == 0 && Float.compare(matrix4f.m01, this.m01) == 0
					&& Float.compare(matrix4f.m02, this.m02) == 0 && Float.compare(matrix4f.m03, this.m03) == 0
					&& Float.compare(matrix4f.m10, this.m10) == 0 && Float.compare(matrix4f.m11, this.m11) == 0
					&& Float.compare(matrix4f.m12, this.m12) == 0 && Float.compare(matrix4f.m13, this.m13) == 0
					&& Float.compare(matrix4f.m20, this.m20) == 0 && Float.compare(matrix4f.m21, this.m21) == 0
					&& Float.compare(matrix4f.m22, this.m22) == 0 && Float.compare(matrix4f.m23, this.m23) == 0
					&& Float.compare(matrix4f.m30, this.m30) == 0 && Float.compare(matrix4f.m31, this.m31) == 0
					&& Float.compare(matrix4f.m32, this.m32) == 0 && Float.compare(matrix4f.m33, this.m33) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
		i = 31 * i + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
		i = 31 * i + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
		i = 31 * i + (this.m03 != 0.0F ? Float.floatToIntBits(this.m03) : 0);
		i = 31 * i + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
		i = 31 * i + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
		i = 31 * i + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
		i = 31 * i + (this.m13 != 0.0F ? Float.floatToIntBits(this.m13) : 0);
		i = 31 * i + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
		i = 31 * i + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
		i = 31 * i + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
		i = 31 * i + (this.m23 != 0.0F ? Float.floatToIntBits(this.m23) : 0);
		i = 31 * i + (this.m30 != 0.0F ? Float.floatToIntBits(this.m30) : 0);
		i = 31 * i + (this.m31 != 0.0F ? Float.floatToIntBits(this.m31) : 0);
		i = 31 * i + (this.m32 != 0.0F ? Float.floatToIntBits(this.m32) : 0);
		return 31 * i + (this.m33 != 0.0F ? Float.floatToIntBits(this.m33) : 0);
	}

	//private static int bufferIndex(int raw, int col) {
	//	return col << 2 + raw;
	//}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("Matrix4f:\n");
		stringbuilder.append(this.m00);
		stringbuilder.append(" ");
		stringbuilder.append(this.m01);
		stringbuilder.append(" ");
		stringbuilder.append(this.m02);
		stringbuilder.append(" ");
		stringbuilder.append(this.m03);
		stringbuilder.append("\n");
		stringbuilder.append(this.m10);
		stringbuilder.append(" ");
		stringbuilder.append(this.m11);
		stringbuilder.append(" ");
		stringbuilder.append(this.m12);
		stringbuilder.append(" ");
		stringbuilder.append(this.m13);
		stringbuilder.append("\n");
		stringbuilder.append(this.m20);
		stringbuilder.append(" ");
		stringbuilder.append(this.m21);
		stringbuilder.append(" ");
		stringbuilder.append(this.m22);
		stringbuilder.append(" ");
		stringbuilder.append(this.m23);
		stringbuilder.append("\n");
		stringbuilder.append(this.m30);
		stringbuilder.append(" ");
		stringbuilder.append(this.m31);
		stringbuilder.append(" ");
		stringbuilder.append(this.m32);
		stringbuilder.append(" ");
		stringbuilder.append(this.m33);
		stringbuilder.append("\n");
		return stringbuilder.toString();
	}

	public Matrix4f write(ByteBuffer floatBufferIn) {
		floatBufferIn.putFloat(4 * 0, this.m00);
		floatBufferIn.putFloat(4 * 1, this.m10);
		floatBufferIn.putFloat(4 * 2, this.m20);
		floatBufferIn.putFloat(4 * 3, this.m30);
		floatBufferIn.putFloat(4 * 4, this.m01);
		floatBufferIn.putFloat(4 * 5, this.m11);
		floatBufferIn.putFloat(4 * 6, this.m21);
		floatBufferIn.putFloat(4 * 7, this.m31);
		floatBufferIn.putFloat(4 * 8, this.m02);
		floatBufferIn.putFloat(4 * 9, this.m12);
		floatBufferIn.putFloat(4 * 10, this.m22);
		floatBufferIn.putFloat(4 * 11, this.m32);
		floatBufferIn.putFloat(4 * 12, this.m03);
		floatBufferIn.putFloat(4 * 13, this.m13);
		floatBufferIn.putFloat(4 * 14, this.m23);
		floatBufferIn.putFloat(4 * 15, this.m33);
		return this;
	}

	public Matrix4f setIdentity() {
		this.m00 = 1.0F;
		this.m01 = 0.0F;
		this.m02 = 0.0F;
		this.m03 = 0.0F;
		this.m10 = 0.0F;
		this.m11 = 1.0F;
		this.m12 = 0.0F;
		this.m13 = 0.0F;
		this.m20 = 0.0F;
		this.m21 = 0.0F;
		this.m22 = 1.0F;
		this.m23 = 0.0F;
		this.m30 = 0.0F;
		this.m31 = 0.0F;
		this.m32 = 0.0F;
		this.m33 = 1.0F;
		return this;
	}

	public float adjugateAndDet() {
		float f = this.m00 * this.m11 - this.m01 * this.m10;
		float f1 = this.m00 * this.m12 - this.m02 * this.m10;
		float f2 = this.m00 * this.m13 - this.m03 * this.m10;
		float f3 = this.m01 * this.m12 - this.m02 * this.m11;
		float f4 = this.m01 * this.m13 - this.m03 * this.m11;
		float f5 = this.m02 * this.m13 - this.m03 * this.m12;
		float f6 = this.m20 * this.m31 - this.m21 * this.m30;
		float f7 = this.m20 * this.m32 - this.m22 * this.m30;
		float f8 = this.m20 * this.m33 - this.m23 * this.m30;
		float f9 = this.m21 * this.m32 - this.m22 * this.m31;
		float f10 = this.m21 * this.m33 - this.m23 * this.m31;
		float f11 = this.m22 * this.m33 - this.m23 * this.m32;
		float f12 = this.m11 * f11 - this.m12 * f10 + this.m13 * f9;
		float f13 = -this.m10 * f11 + this.m12 * f8 - this.m13 * f7;
		float f14 = this.m10 * f10 - this.m11 * f8 + this.m13 * f6;
		float f15 = -this.m10 * f9 + this.m11 * f7 - this.m12 * f6;
		float f16 = -this.m01 * f11 + this.m02 * f10 - this.m03 * f9;
		float f17 = this.m00 * f11 - this.m02 * f8 + this.m03 * f7;
		float f18 = -this.m00 * f10 + this.m01 * f8 - this.m03 * f6;
		float f19 = this.m00 * f9 - this.m01 * f7 + this.m02 * f6;
		float f20 = this.m31 * f5 - this.m32 * f4 + this.m33 * f3;
		float f21 = -this.m30 * f5 + this.m32 * f2 - this.m33 * f1;
		float f22 = this.m30 * f4 - this.m31 * f2 + this.m33 * f;
		float f23 = -this.m30 * f3 + this.m31 * f1 - this.m32 * f;
		float f24 = -this.m21 * f5 + this.m22 * f4 - this.m23 * f3;
		float f25 = this.m20 * f5 - this.m22 * f2 + this.m23 * f1;
		float f26 = -this.m20 * f4 + this.m21 * f2 - this.m23 * f;
		float f27 = this.m20 * f3 - this.m21 * f1 + this.m22 * f;
		this.m00 = f12;
		this.m10 = f13;
		this.m20 = f14;
		this.m30 = f15;
		this.m01 = f16;
		this.m11 = f17;
		this.m21 = f18;
		this.m31 = f19;
		this.m02 = f20;
		this.m12 = f21;
		this.m22 = f22;
		this.m32 = f23;
		this.m03 = f24;
		this.m13 = f25;
		this.m23 = f26;
		this.m33 = f27;
		return f * f11 - f1 * f10 + f2 * f9 + f3 * f8 - f4 * f7 + f5 * f6;
	}

	public float determinant() {
		float f = this.m00 * this.m11 - this.m01 * this.m10;
		float g = this.m00 * this.m12 - this.m02 * this.m10;
		float h = this.m00 * this.m13 - this.m03 * this.m10;
		float i = this.m01 * this.m12 - this.m02 * this.m11;
		float j = this.m01 * this.m13 - this.m03 * this.m11;
		float k = this.m02 * this.m13 - this.m03 * this.m12;
		float l = this.m20 * this.m31 - this.m21 * this.m30;
		float m = this.m20 * this.m32 - this.m22 * this.m30;
		float n = this.m20 * this.m33 - this.m23 * this.m30;
		float o = this.m21 * this.m32 - this.m22 * this.m31;
		float p = this.m21 * this.m33 - this.m23 * this.m31;
		float q = this.m22 * this.m33 - this.m23 * this.m32;
		return f * q - g * p + h * o + i * n - j * m + k * l;
	}

	public Matrix4f translate(double x, double y, double z) {
		return translate((float) x, (float) y, (float) z);
	}

	public Matrix4f translate(float x, float y, float z) {
		multiplyByTranslation(x, y, z);
		return this;
	}

	public Matrix4f translateAbsolute(double x, double y, double z) {
		return translateAbsolute((float) x, (float) y, (float) z);
	}

	public Matrix4f translateAbsolute(float x, float y, float z) {
		m03 += x;
		m13 += y;
		m23 += z;
		return this;
	}

	public Matrix4f scale(float x, float y, float z) {
		mul(makeScale(x, y, z));
		return this;
	}

	public Matrix4f scale(double x, double y, double z) {
		return scale((float) x, (float) y, (float) z);
	}

	public Matrix4f transpose() {
		float f = this.m10;
		this.m10 = this.m01;
		this.m01 = f;
		f = this.m20;
		this.m20 = this.m02;
		this.m02 = f;
		f = this.m21;
		this.m21 = this.m12;
		this.m12 = f;
		f = this.m30;
		this.m30 = this.m03;
		this.m03 = f;
		f = this.m31;
		this.m31 = this.m13;
		this.m13 = f;
		f = this.m32;
		this.m32 = this.m23;
		this.m23 = f;
		return this;
	}

	public Matrix4f inverse() {
		float f = this.adjugateAndDet();
		if (Math.abs(f) > 1.0E-16F) {
			this.mul(1 / f);
		}
		return this;
	}

	/**
	 * Multiply self on the right by the parameter
	 */
	public Matrix4f mul(Matrix4f matrix) {
		float f = this.m00 * matrix.m00 + this.m01 * matrix.m10 + this.m02 * matrix.m20 + this.m03 * matrix.m30;
		float f1 = this.m00 * matrix.m01 + this.m01 * matrix.m11 + this.m02 * matrix.m21 + this.m03 * matrix.m31;
		float f2 = this.m00 * matrix.m02 + this.m01 * matrix.m12 + this.m02 * matrix.m22 + this.m03 * matrix.m32;
		float f3 = this.m00 * matrix.m03 + this.m01 * matrix.m13 + this.m02 * matrix.m23 + this.m03 * matrix.m33;
		float f4 = this.m10 * matrix.m00 + this.m11 * matrix.m10 + this.m12 * matrix.m20 + this.m13 * matrix.m30;
		float f5 = this.m10 * matrix.m01 + this.m11 * matrix.m11 + this.m12 * matrix.m21 + this.m13 * matrix.m31;
		float f6 = this.m10 * matrix.m02 + this.m11 * matrix.m12 + this.m12 * matrix.m22 + this.m13 * matrix.m32;
		float f7 = this.m10 * matrix.m03 + this.m11 * matrix.m13 + this.m12 * matrix.m23 + this.m13 * matrix.m33;
		float f8 = this.m20 * matrix.m00 + this.m21 * matrix.m10 + this.m22 * matrix.m20 + this.m23 * matrix.m30;
		float f9 = this.m20 * matrix.m01 + this.m21 * matrix.m11 + this.m22 * matrix.m21 + this.m23 * matrix.m31;
		float f10 = this.m20 * matrix.m02 + this.m21 * matrix.m12 + this.m22 * matrix.m22 + this.m23 * matrix.m32;
		float f11 = this.m20 * matrix.m03 + this.m21 * matrix.m13 + this.m22 * matrix.m23 + this.m23 * matrix.m33;
		float f12 = this.m30 * matrix.m00 + this.m31 * matrix.m10 + this.m32 * matrix.m20 + this.m33 * matrix.m30;
		float f13 = this.m30 * matrix.m01 + this.m31 * matrix.m11 + this.m32 * matrix.m21 + this.m33 * matrix.m31;
		float f14 = this.m30 * matrix.m02 + this.m31 * matrix.m12 + this.m32 * matrix.m22 + this.m33 * matrix.m32;
		float f15 = this.m30 * matrix.m03 + this.m31 * matrix.m13 + this.m32 * matrix.m23 + this.m33 * matrix.m33;
		this.m00 = f;
		this.m01 = f1;
		this.m02 = f2;
		this.m03 = f3;
		this.m10 = f4;
		this.m11 = f5;
		this.m12 = f6;
		this.m13 = f7;
		this.m20 = f8;
		this.m21 = f9;
		this.m22 = f10;
		this.m23 = f11;
		this.m30 = f12;
		this.m31 = f13;
		this.m32 = f14;
		this.m33 = f15;
		return this;
	}

	public Matrix4f mul(Quat quaternion) {
		this.mul(new Matrix4f(quaternion));
		return this;
	}

	public Matrix4f mul(Quatf quaternion) {
		this.mul(new Matrix4f(quaternion));
		return this;
	}

	public Matrix4f mul(float scale) {
		this.m00 *= scale;
		this.m01 *= scale;
		this.m02 *= scale;
		this.m03 *= scale;
		this.m10 *= scale;
		this.m11 *= scale;
		this.m12 *= scale;
		this.m13 *= scale;
		this.m20 *= scale;
		this.m21 *= scale;
		this.m22 *= scale;
		this.m23 *= scale;
		this.m30 *= scale;
		this.m31 *= scale;
		this.m32 *= scale;
		this.m33 *= scale;
		return this;
	}

	public static Matrix4f perspective(double fov, float aspectRatio, float nearPlane, float farPlane) {
		float f = (float) (1.0D / Math.tan(fov * (double) ((float) Math.PI / 180F) / 2.0D));
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = f / aspectRatio;
		matrix4f.m11 = f;
		matrix4f.m22 = (farPlane + nearPlane) / (nearPlane - farPlane);
		matrix4f.m33 = 0.0F;
		matrix4f.m32 = -1.0F;
		matrix4f.m23 = 2.0F * farPlane * nearPlane / (nearPlane - farPlane);
		return matrix4f;
	}

	public static Matrix4f perspectiveInfinity(double fov, float aspectRatio, float nearPlane) {
		float f = (float) (1.0D / Math.tan(fov * (double) ((float) Math.PI / 180F) / 2.0D));
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = f / aspectRatio;
		matrix4f.m11 = f;
		matrix4f.m22 = -1.0F;
		matrix4f.m33 = 0.0F;
		matrix4f.m32 = -1.0F;
		matrix4f.m23 = -2.0F * nearPlane;
		return matrix4f;
	}

	public static Matrix4f orthographic(float width, float height, float nearPlane, float farPlane) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = 2.0F / width;
		matrix4f.m11 = 2.0F / height;
		float f = farPlane - nearPlane;
		matrix4f.m22 = -2.0F / f;
		matrix4f.m33 = 1.0F;
		matrix4f.m03 = -1.0F;
		matrix4f.m13 = -1.0F;
		matrix4f.m23 = -(farPlane + nearPlane) / f;
		return matrix4f;
	}

	public Matrix4f translate(Vec3f vector) {
		this.m03 += vector.x;
		this.m13 += vector.y;
		this.m23 += vector.z;
		return this;
	}

	public Matrix4f translate(Vec3 vector) {
		this.m03 += vector.x;
		this.m13 += vector.y;
		this.m23 += vector.z;
		return this;
	}

	public Matrix4f copy() {
		return new Matrix4f(this);
	}

	public Matrix4f(float[] values) {
		m00 = values[0];
		m01 = values[1];
		m02 = values[2];
		m03 = values[3];
		m10 = values[4];
		m11 = values[5];
		m12 = values[6];
		m13 = values[7];
		m20 = values[8];
		m21 = values[9];
		m22 = values[10];
		m23 = values[11];
		m30 = values[12];
		m31 = values[13];
		m32 = values[14];
		m33 = values[15];
	}

	public Matrix4f set(Matrix4f mat) {
		this.m00 = mat.m00;
		this.m01 = mat.m01;
		this.m02 = mat.m02;
		this.m03 = mat.m03;
		this.m10 = mat.m10;
		this.m11 = mat.m11;
		this.m12 = mat.m12;
		this.m13 = mat.m13;
		this.m20 = mat.m20;
		this.m21 = mat.m21;
		this.m22 = mat.m22;
		this.m23 = mat.m23;
		this.m30 = mat.m30;
		this.m31 = mat.m31;
		this.m32 = mat.m32;
		this.m33 = mat.m33;
		return this;
	}

	public Matrix4f add(Matrix4f other) {
		m00 += other.m00;
		m01 += other.m01;
		m02 += other.m02;
		m03 += other.m03;
		m10 += other.m10;
		m11 += other.m11;
		m12 += other.m12;
		m13 += other.m13;
		m20 += other.m20;
		m21 += other.m21;
		m22 += other.m22;
		m23 += other.m23;
		m30 += other.m30;
		m31 += other.m31;
		m32 += other.m32;
		m33 += other.m33;
		return this;
	}

	public Matrix4f multiplyBackward(Matrix4f other) {
		Matrix4f copy = other.copy();
		copy.mul(this);
		this.set(copy);
		return this;
	}

	public Matrix4f multiplyByTranslation(float x, float y, float z) {
		this.m03 = this.m00 * x + this.m01 * y + this.m02 * z + this.m03;
		this.m13 = this.m10 * x + this.m11 * y + this.m12 * z + this.m13;
		this.m23 = this.m20 * x + this.m21 * y + this.m22 * z + this.m23;
		this.m33 = this.m30 * x + this.m31 * y + this.m32 * z + this.m33;
		return this;
	}

	public static Matrix4f makeScale(float x, float y, float z) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.m00 = x;
		matrix4f.m11 = y;
		matrix4f.m22 = z;
		//matrix4f.m33 = 1.0f;
		return matrix4f;
	}

	public static Matrix4f makeTranslation(float x, float y, float z) {
		Matrix4f matrix4f = new Matrix4f();
		//this.m00 = 1.0F;
		//this.m11 = 1.0F;
		//this.m22 = 1.0F;
		//this.m33 = 1.0F;
		matrix4f.m03 = x;
		matrix4f.m13 = y;
		matrix4f.m23 = z;
		return matrix4f;
	}

	public Matrix4f setTranslation(float x, float y, float z) {
		//this.m00 = 1.0F;
		//this.m11 = 1.0F;
		//this.m22 = 1.0F;
		//this.m33 = 1.0F;
		this.m03 = x;
		this.m13 = y;
		this.m23 = z;
		return this;
	}
}
