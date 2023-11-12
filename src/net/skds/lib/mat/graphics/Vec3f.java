package net.skds.lib.mat.graphics;

import net.skds.lib.mat.IVec3;
import net.skds.lib.mat.Vec3;

public class Vec3f implements IVec3 {

	public static final Vec3f XN = new Vec3f(-1.0F, 0.0F, 0.0F);
	public static final Vec3f XP = new Vec3f(1.0F, 0.0F, 0.0F);
	public static final Vec3f YN = new Vec3f(0.0F, -1.0F, 0.0F);
	public static final Vec3f YP = new Vec3f(0.0F, 1.0F, 0.0F);
	public static final Vec3f ZN = new Vec3f(0.0F, 0.0F, -1.0F);
	public static final Vec3f ZP = new Vec3f(0.0F, 0.0F, 1.0F);
	public static final Vec3f SINGLE = new Vec3f(1.0f, 1.0f, 1.0f);
	public static final Vec3f ZERO = new Vec3f();

	public float x;
	public float y;
	public float z;

	public static Vec3f XP() {
		return XP.copy();
	}

	public static Vec3f XN() {
		return XN.copy();
	}

	public static Vec3f YP() {
		return YP.copy();
	}

	public static Vec3f YN() {
		return YN.copy();
	}

	public static Vec3f ZP() {
		return ZP.copy();
	}

	public static Vec3f ZN() {
		return ZN.copy();
	}

	public static Vec3f ZERO() {
		return new Vec3f();
	}

	public static Vec3f SINGLE() {
		return SINGLE.copy();
	}

	public Vec3f() {
	}

	public Vec3f(float[] arr) {
		if (arr == null || arr.length < 3) {
			return;
		}
		this.x = arr[0];
		this.y = arr[1];
		this.z = arr[2];
	}

	public Vec3f(Vec3 v3) {
		this.x = (float) v3.x;
		this.y = (float) v3.y;
		this.z = (float) v3.z;
	}

	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3f transform(Matrix3f matrixIn) {
		float f = this.x;
		float f1 = this.y;
		float f2 = this.z;
		this.x = matrixIn.m00 * f + matrixIn.m01 * f1 + matrixIn.m02 * f2;
		this.y = matrixIn.m10 * f + matrixIn.m11 * f1 + matrixIn.m12 * f2;
		this.z = matrixIn.m20 * f + matrixIn.m21 * f1 + matrixIn.m22 * f2;
		return this;
	}

	public Vec3f transform(Quatf quaternionIn) {
		Quatf q = quaternionIn.copy().multiplyQ(this.x, this.y, this.z, 0.0F);
		Quatf q2 = quaternionIn.copy().conjugate();
		q.multiply(q2);
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		return this;
	}

	public Quatf rotationDegrees(float valueIn) {
		return new Quatf(this, valueIn, true);
	}

	public Vec3f lerp(Vec3f vectorIn, float pctIn) {
		this.x += vectorIn.x * pctIn;
		this.y += vectorIn.y * pctIn;
		this.z += vectorIn.z * pctIn;
		return this;
	}

	public Vec3f set(Vec3f vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}

	public Vec3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vec3f sub(Vec3f vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}

	public Vec3f sub(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vec3f normalize() {
		float d0 = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		if (d0 < 1E-60) {
			this.x = 0;
			this.y = 1;
			this.z = 0;
			return this;
		}
		this.x /= d0;
		this.y /= d0;
		this.z /= d0;
		return this;
	}

	public static float staticDot(float x, float y, float z, Vec3f vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	public float dot(Vec3f vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}

	public Vec3f cross(Vec3f vec) {
		float x = this.y * vec.z - this.z * vec.y;
		float y = this.z * vec.x - this.x * vec.z;
		float z = this.x * vec.y - this.y * vec.x;
		this.y = y;
		this.z = z;
		this.x = x;
		return this;
	}

	public Vec3f add(Vec3f vec) {
		return add(vec.x, vec.y, vec.z);
	}

	public Vec3f moveNorm(Vec3f norm, float dist) {
		return add(norm.x * dist, norm.y * dist, norm.z * dist);
	}

	public Vec3f add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public float distanceTo(Vec3f vec) {
		float d0 = vec.x - this.x;
		float d1 = vec.y - this.y;
		float d2 = vec.z - this.z;
		return (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	public float squareDistanceTo(Vec3f vec) {
		float d0 = vec.x - this.x;
		float d1 = vec.y - this.y;
		float d2 = vec.z - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public float squareDistanceTo(float xIn, float yIn, float zIn) {
		float d0 = xIn - this.x;
		float d1 = yIn - this.y;
		float d2 = zIn - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public Vec3f scale(float factor) {
		return this.mul(factor, factor, factor);
	}

	public Vec3f inverse() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vec3f mul(Vec3f vec) {
		return this.mul(vec.x, vec.y, vec.z);
	}

	public Vec3f mul(float factorX, float factorY, float factorZ) {
		this.x *= factorX;
		this.y *= factorY;
		this.z *= factorZ;
		return this;
	}

	public float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public float lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Vec3f)) {
			return false;
		} else {
			Vec3f Vec3d = (Vec3f) o;
			if (Float.compare(Vec3d.x, this.x) != 0) {
				return false;
			} else if (Float.compare(Vec3d.y, this.y) != 0) {
				return false;
			} else {
				return Float.compare(Vec3d.z, this.z) == 0;
			}
		}
	}

	public int hashCode() {
		long j = Float.floatToIntBits(this.x);
		int i = (int) (j ^ j >>> 32);
		j = Float.floatToIntBits(this.y);
		i = 31 * i + (int) (j ^ j >>> 32);
		j = Float.floatToIntBits(this.z);
		return 31 * i + (int) (j ^ j >>> 32);
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public Vec3f rotatePitch(float pitch) {
		float f = (float) Math.cos(pitch);
		float f1 = (float) Math.sin(pitch);
		float d0 = this.x;
		float d1 = this.y * f + this.z * f1;
		float d2 = this.z * f - this.y * f1;
		this.x = d0;
		this.y = d1;
		this.z = d2;
		return this;
	}

	public Vec3f rotateYaw(float yaw) {
		float f = (float) Math.cos(yaw);
		float f1 = (float) Math.sin(yaw);
		float d0 = this.x * f + this.z * f1;
		float d1 = this.y;
		float d2 = this.z * f - this.x * f1;
		this.x = d0;
		this.y = d1;
		this.z = d2;
		return this;
	}

	public Vec3f rotateRoll(float roll) {
		float f = (float) Math.cos(roll);
		float f1 = (float) Math.sin(roll);
		float d0 = this.x * f + this.y * f1;
		float d1 = this.y * f - this.x * f1;
		float d2 = this.z;
		this.x = d0;
		this.y = d1;
		this.z = d2;
		return this;
	}

	public float projOn(Vec3f a) {
		return projOnNormal(a.copy().normalize());

	}

	public float projOnNormal(Vec3f a) {
		return dot(a);
	}

	public Vec3f copy() {
		return new Vec3f(x, y, z);
	}

	public Vec3f flip() {
		x = x != 0 ? 1f / x : maxim(x);
		y = y != 0 ? 1f / y : maxim(y);
		z = z != 0 ? 1f / z : maxim(z);
		return this;
	}

	private static float maxim(float d) {
		final float max = Float.MAX_VALUE / 4;
		if (d > 0) {
			return max;
		} else {
			return -max;
		}
	}

	public Vec3f aprZero(float lim) {

		if (Math.abs(x) < lim) {
			x = 0;
		}
		if (Math.abs(y) < lim) {
			y = 0;
		}
		if (Math.abs(z) < lim) {
			z = 0;
		}

		return this;
	}

	public static Vec3f avg(Vec3f... vecs) {
		Vec3f ret = new Vec3f();
		int c = 0;

		for (Vec3f vec : vecs) {
			if (vec != null) {
				c++;
				ret.add(vec);
			}
		}

		if (c == 0) {
			return Vec3f.ZERO();
		}

		return ret.div(c);
	}

	public Vec3f div(Vec3f c) {
		this.x /= c.x;
		this.y /= c.y;
		this.z /= c.z;
		return this;
	}

	public Vec3f div(float c) {
		this.x /= c;
		this.y /= c;
		this.z /= c;
		return this;
	}

	public float[] asArray() {
		return new float[]{x, y, z};
	}

	@Override
	public double x() {
		return x;
	}

	@Override
	public double y() {
		return y;
	}

	@Override
	public double z() {
		return z;
	}

	//public Vec3d getMoj() {
	//	return new Vec3d(x, y, z);
	//}

}