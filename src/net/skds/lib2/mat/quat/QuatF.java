package net.skds.lib2.mat.quat;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;

public record QuatF(float xf, float yf, float zf, float wf) implements Quat {

	public static final QuatF ONE = new QuatF(0, 0, 0, 1);

	@Override
	public double x() {
		return xf;
	}

	@Override
	public double y() {
		return yf;
	}

	@Override
	public double z() {
		return zf;
	}

	@Override
	public double w() {
		return wf;
	}

	@Override
	public QuatF multiplyF(Quat q) {
		return q instanceof QuatF qf ? multiplyF(qf) : multiplyF(q.xf(), q.yf(), q.zf(), q.wf());
	}

	@Override
	public QuatD multiply(Quat q) {
		if (q instanceof QuatD(double x, double y, double z, double w)) {
			return multiply(x, y, z, w);
		}
		return multiply(q.x(), q.y(), q.z(), q.w());
	}

	public QuatF multiplyF(QuatF q) {
		return multiplyF(q.xf, q.yf, q.zf, q.wf);
	}

	public QuatF multiplyF(float qx, float qy, float qz, float qw) {
		return new QuatF(
				wf * qx + xf * qw + yf * qz - zf * qy,
				wf * qy - xf * qz + yf * qw + zf * qx,
				wf * qz + xf * qy - yf * qx + zf * qw,
				wf * qw - xf * qx - yf * qy - zf * qz
		);
	}

	public QuatD multiply(double qx, double qy, double qz, double qw) {
		return new QuatD(
				wf * qx + xf * qw + yf * qz - zf * qy,
				wf * qy - xf * qz + yf * qw + zf * qx,
				wf * qz + xf * qy - yf * qx + zf * qw,
				wf * qw - xf * qx - yf * qy - zf * qz
		);
	}

	@Override
	public QuatF rotateAxisDegreesF(Vec3 axis, float angle) {
		float f0 = FastMath.sinDegr(angle / 2.0f);
		return multiplyF(axis.xf() * f0, axis.yf() * f0, axis.zf() * f0, FastMath.cosDegr(angle / 2.0f));
	}

	@Override
	public QuatF rotateAxisDegrees(Vec3 axis, double angle) {
		return rotateAxisDegreesF(axis, (float) angle);
	}

	@Override
	public QuatF spinF(Vec3 spin) {
		float angle = spin.lengthF();
		if (angle < 1E-7f) return this;
		float f0 = FastMath.sinRad(angle / 2.0f);
		return multiplyF(
				spin.xf() * f0 / angle,
				spin.yf() * f0 / angle,
				spin.zf() * f0 / angle,
				FastMath.cosRad(angle / 2.0f)
		);
	}

	@Override
	public QuatF spin(Vec3 spin) {
		return spinF(spin);
	}

	@Override
	public QuatF conjugateF() {
		return new QuatF(-xf, -yf, -zf, wf);
	}

	@Override
	public QuatF conjugate() {
		return new QuatF(-xf, -yf, -zf, wf);
	}

	@Override
	public QuatF normalizeF() {
		float g = FastMath.invSqrt(xf * xf + yf * yf + zf * zf + wf * wf + Float.MIN_NORMAL);
		return new QuatF(xf * g, yf * g, zf * g, wf * g);
	}

	@Override
	public QuatD normalize() {
		double g = FastMath.invSqrt((double) xf * xf + (double) yf * yf + (double) zf * zf + (double) wf * wf + Double.MIN_NORMAL);
		return new QuatD(xf * g, yf * g, zf * g, wf * g);
	}

	@Override
	public Vec3F forwardF() {
		return new Vec3F(
				2.0f * (zf * xf + yf * wf),
				2.0f * (yf * zf - xf * wf),
				1.0f - 2.0f * xf * xf - 2.0f * yf * yf
		);
	}

	@Override
	public Vec3D forward() {
		return new Vec3D(
				2.0 * (x() * z() + y() * w()),
				2.0 * (y() * z() - x() * w()),
				1.0 - 2.0 * x() * x() - 2.0 * y() * y()
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		return obj instanceof Quat q && switch (q) {
			case QuatF(float x2, float y2, float z2, float w2) ->
					this.xf == x2 && this.yf == y2 && this.zf == z2 && this.wf == w2;
			case QuatD(double x2, double y2, double z2, double w2) ->
					(double) this.xf == x2 && (double) this.yf == y2 && (double) this.zf == z2 && (double) this.wf == w2;
		};
	}

	@Override
	public int hashCode() {
		return Quat.hashCode(this);
	}
}
