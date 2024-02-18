package net.skds.lib.mat;

import net.skds.lib.mat.graphics.Vec3f;
import net.skds.lib.utils.SKDSUtils;

import java.util.Random;

public final class Vec3 implements IVec3 {

	public static final Vec3 XN = new Vec3(-1.0F, 0.0F, 0.0F);
	public static final Vec3 XP = new Vec3(1.0F, 0.0F, 0.0F);
	public static final Vec3 YN = new Vec3(0.0F, -1.0F, 0.0F);
	public static final Vec3 YP = new Vec3(0.0F, 1.0F, 0.0F);
	public static final Vec3 ZN = new Vec3(0.0F, 0.0F, -1.0F);
	public static final Vec3 ZP = new Vec3(0.0F, 0.0F, 1.0F);
	public static final Vec3 SINGLE = new Vec3(1.0D, 1.0D, 1.0D);
	public static final Vec3 ZERO = new Vec3(0.0D, 0.0D, 0.0D);

	public double x;
	public double y;
	public double z;

	public static Vec3 XP() {
		return XP.copy();
	}

	public static Vec3 XN() {
		return XN.copy();
	}

	public static Vec3 YP() {
		return YP.copy();
	}

	public static Vec3 YN() {
		return YN.copy();
	}

	public static Vec3 ZP() {
		return ZP.copy();
	}

	public static Vec3 ZN() {
		return ZN.copy();
	}

	public static Vec3 ZERO() {
		return new Vec3();
	}

	public static Vec3 SINGLE() {
		return SINGLE.copy();
	}

	public static Vec3 randomNormal(Random r) {
		return new Vec3(r.nextFloat() - .5, r.nextFloat() - .5, r.nextFloat() - .5).normalize();
	}

	public Vec3() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vec3(IVec3 frorward) {
		this.x = frorward.x();
		this.y = frorward.y();
		this.z = frorward.z();
	}

	//public Vec3(Vec3d moj) {
	//	this.x = moj.x;
	//	this.y = moj.y;
	//	this.z = moj.z;
	//}

	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3(double size) {
		this.x = size;
		this.y = size;
		this.z = size;
	}

	public Vec3 transform(Matrix3 matrixIn) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		this.x = matrixIn.m00 * x + matrixIn.m01 * y + matrixIn.m02 * z;
		this.y = matrixIn.m10 * x + matrixIn.m11 * y + matrixIn.m12 * z;
		this.z = matrixIn.m20 * x + matrixIn.m21 * y + matrixIn.m22 * z;
		return this;
	}

	public Vec3 transform(Quat quaternionIn) {
		Quat q = quaternionIn.copy().multiplyQ(this.x, this.y, this.z, 0.0F);
		Quat q2 = quaternionIn.copy().conjugate();
		q.multiply(q2);
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		return this;
	}

	public Quat rotationDegrees(Double valueIn) {
		return new Quat(this, valueIn, true);
	}

	public Vec3 lerp(Vec3 vectorIn, float pctIn) {
		this.x += vectorIn.x * pctIn;
		this.y += vectorIn.y * pctIn;
		this.z += vectorIn.z * pctIn;
		return this;
	}

	public Vec3 set(Vec3 vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}

	public Vec3 clamp(double min, double max) {
		if (this.x > max) {
			this.x = max;
		} else if (this.x < min) {
			this.x = min;
		}
		if (this.y > max) {
			this.y = max;
		} else if (this.y < min) {
			this.y = min;
		}
		if (this.z > max) {
			this.z = max;
		} else if (this.z < min) {
			this.z = min;
		}
		return this;
	}

	public Vec3 set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vec3 sub(Vec3 vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}

	public Vec3 sub(IVec3 vec) {
		this.x -= vec.x();
		this.y -= vec.y();
		this.z -= vec.z();
		return this;
	}

	public Vec3 sub(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vec3 normalize() {
		double d0 = (double) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
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

	public static double staticDot(double x, double y, double z, Vec3 vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	public double dot(Vec3 vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}

	public Vec3 cross(Vec3 vec) {
		double x = this.y * vec.z - this.z * vec.y;
		double y = this.z * vec.x - this.x * vec.z;
		double z = this.x * vec.y - this.y * vec.x;
		this.y = y;
		this.z = z;
		this.x = x;
		return this;
	}

	public Vec3 add(Vec3 vec) {
		return add(vec.x, vec.y, vec.z);
	}

	public Vec3 add(Vec3f vec) {
		return add(vec.x, vec.y, vec.z);
	}

	public Vec3 moveNorm(Vec3 norm, double dist) {
		return add(norm.x * dist, norm.y * dist, norm.z * dist);
	}

	public Vec3 add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public double distanceTo(IVec3 vec) {
		double d0 = vec.x() - this.x;
		double d1 = vec.y() - this.y;
		double d2 = vec.z() - this.z;
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	@Override
	public double distanceTo(double dx, double dy, double dz) {
		double d0 = dx - this.x;
		double d1 = dy - this.y;
		double d2 = dz - this.z;
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	@Override
	public double squareDistanceTo(IVec3 vec) {
		double d0 = vec.x() - this.x;
		double d1 = vec.y() - this.y;
		double d2 = vec.z() - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	@Override
	public double squareDistanceTo(double xIn, double yIn, double zIn) {
		double d0 = xIn - this.x;
		double d1 = yIn - this.y;
		double d2 = zIn - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public Vec3 scale(double factor) {
		return this.mul(factor, factor, factor);
	}

	public Vec3 inverse() {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public Vec3 mul(Vec3 vec) {
		return this.mul(vec.x, vec.y, vec.z);
	}

	public Vec3 mul(double factorX, double factorY, double factorZ) {
		this.x *= factorX;
		this.y *= factorY;
		this.z *= factorZ;
		return this;
	}

	public double length() {
		return (double) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof IVec3 iv3)) {
			return false;
		} else {
			if (iv3.x() != this.x) {
				return false;
			}
			if (iv3.y() != this.y) {
				return false;
			}
			return iv3.z() == this.z;
		}
	}

	public int hashCode() {
		long j = Double.doubleToLongBits(this.x);
		int i = (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.y);
		i = 31 * i + (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.z);
		return 31 * i + (int) (j ^ j >>> 32);
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public Vec3 rotatePitch(float pitch) {
		double f = Math.cos(pitch);
		double f1 = Math.sin(pitch);
		double d0 = this.x;
		double d1 = this.y * f + this.z * f1;
		double d2 = this.z * f - this.y * f1;
		this.x = d0;
		this.y = d1;
		this.z = d2;
		return this;
	}

	public Vec3 rotateYaw(float yaw) {
		double f = Math.cos(yaw);
		double f1 = Math.sin(yaw);
		double d0 = this.x * f + this.z * f1;
		double d1 = this.y;
		double d2 = this.z * f - this.x * f1;
		this.x = d0;
		this.y = d1;
		this.z = d2;
		return this;
	}

	public Vec3 rotateRoll(float roll) {
		double f = Math.cos(roll);
		double f1 = Math.sin(roll);
		double d0 = this.x * f + this.y * f1;
		double d1 = this.y * f - this.x * f1;
		double d2 = this.z;
		this.x = d0;
		this.y = d1;
		this.z = d2;
		return this;
	}

	public double projOn(Vec3 vec) {
		double l = vec.length();
		double ret = this.x * vec.x + this.y * vec.y + this.z * vec.z;
		return l == 0 ? ret : ret / l;

	}

	public double projOnNormal(Vec3 a) {
		return dot(a);
	}

	public Vec3 copy() {
		return new Vec3(x, y, z);
	}

	public Vec3 flip() {
		x = x != 0 ? 1d / x : maxim(x);
		y = y != 0 ? 1d / y : maxim(y);
		z = z != 0 ? 1d / z : maxim(z);
		return this;
	}

	public Vec3 abs() {
		if (x < 0) {
			x = -x;
		}
		if (y < 0) {
			y = -y;
		}
		if (z < 0) {
			z = -z;
		}
		return this;
	}

	public Vec3 greater(double d) {
		if (x < d) {
			x = d;
		}
		if (y < d) {
			y = d;
		}
		if (z < d) {
			z = d;
		}
		return this;
	}

	private static double maxim(double d) {
		final double max = Double.MAX_VALUE / 4;
		if (d > 0) {
			return max;
		} else {
			return -max;
		}
	}

	public Vec3 aprZero(double lim) {

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

	public static Vec3 avg(Vec3... vecs) {
		Vec3 ret = new Vec3();
		int c = 0;

		for (Vec3 vec : vecs) {
			if (vec != null) {
				c++;
				ret.add(vec);
			}
		}

		if (c == 0) {
			return Vec3.ZERO();
		}

		return ret.div(c);
	}

	public Vec3 div(Vec3 c) {
		this.x /= c.x;
		this.y /= c.y;
		this.z /= c.z;
		return this;
	}

	public Vec3 div(double c) {
		this.x /= c;
		this.y /= c;
		this.z /= c;
		return this;
	}

	public Vec3 randomizeGaussian(double fraction) {
		double len = length() * fraction;
		Random r = SKDSUtils.R;
		this.x += r.nextGaussian() * len;
		this.y += r.nextGaussian() * len;
		this.z += r.nextGaussian() * len;
		return this;
	}

	public boolean isAxis() {
		return isAxis(1E-20);
	}

	public boolean isAxis(double tolerance) {
		if (FastMath.approxEqualSq(x, 1, tolerance)) {
			return FastMath.approxEqualSq(y, 0, tolerance) && FastMath.approxEqualSq(z, 0, tolerance);
		} else if (FastMath.approxEqualSq(y, 1, tolerance)) {
			return FastMath.approxEqualSq(x, 0, tolerance) && FastMath.approxEqualSq(z, 0, tolerance);
		} else if (FastMath.approxEqualSq(z, 1, tolerance)) {
			return FastMath.approxEqualSq(y, 0, tolerance) && FastMath.approxEqualSq(x, 0, tolerance);
		}
		return false;
	}


	public double[] asArray() {
		return new double[]{x, y, z};
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