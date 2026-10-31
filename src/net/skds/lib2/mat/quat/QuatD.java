package net.skds.lib2.mat.quat;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;

public record QuatD(double x, double y, double z, double w) implements Quat {

	@Override
	public QuatD multiply(Quat q) {
		return q instanceof QuatD qd ? multiply(qd) : multiply(q.x(), q.y(), q.z(), q.w());
	}

	@Override
	public QuatD multiplyF(Quat q) {
		return multiply(q);
	}

	public QuatD multiply(QuatD q) {
		return multiply(q.x, q.y, q.z, q.w);
	}

	public QuatD multiply(double qx, double qy, double qz, double qw) {
		return new QuatD(
				w * qx + x * qw + y * qz - z * qy,
				w * qy - x * qz + y * qw + z * qx,
				w * qz + x * qy - y * qx + z * qw,
				w * qw - x * qx - y * qy - z * qz
		);
	}

	@Override
	public QuatD rotateAxisDegrees(Vec3 axis, double angle) {
		double f0 = FastMath.sinDegr(angle / 2.0);
		return multiply(axis.x() * f0, axis.y() * f0, axis.z() * f0, FastMath.cosDegr(angle / 2.0));
	}

	@Override
	public QuatD rotateAxisDegreesF(Vec3 axis, float angle) {
		return rotateAxisDegrees(axis, angle);
	}

	@Override
	public QuatD spin(Vec3 spin) {
		double angle = spin.length();
		if (angle < 1E-7) return this;
		double f0 = FastMath.sinRad(angle / 2.0);
		return multiply(
				spin.x() * f0 / angle,
				spin.y() * f0 / angle,
				spin.z() * f0 / angle,
				FastMath.cosRad(angle / 2.0)
		);
	}

	@Override
	public QuatD spinF(Vec3 spin) {
		return spin(spin);
	}

	@Override
	public QuatD conjugate() {
		return new QuatD(-x, -y, -z, w);
	}

	@Override
	public QuatD conjugateF() {
		return conjugate();
	}

	@Override
	public QuatD normalize() {
		double g = FastMath.invSqrt(x * x + y * y + z * z + w * w + Double.MIN_NORMAL);
		return new QuatD(x * g, y * g, z * g, w * g);
	}

	@Override
	public QuatD normalizeF() {
		return normalize();
	}

	@Override
	public Vec3D forward() {
		return new Vec3D(2.0 * (z * x + y * w), 2.0 * (y * z - x * w), 1.0 - 2.0 * x * x - 2.0 * y * y);
	}

	@Override
	public Vec3D forwardF() {
		return forward();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		return obj instanceof Quat q && switch (q) {
			case QuatD(double x2, double y2, double z2, double w2) ->
					this.x == x2 && this.y == y2 && this.z == z2 && this.w == w2;
			case QuatF(float x2, float y2, float z2, float w2) ->
					this.x == x2 && this.y == y2 && this.z == z2 && this.w == w2;
		};
	}

	@Override
	public int hashCode() {
		return Quat.hashCode(this);
	}
}
