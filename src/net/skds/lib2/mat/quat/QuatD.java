package net.skds.lib2.mat.quat;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;

public record QuatD(double x, double y, double z, double w) implements Quat {

	@Override
	public QuatD multiply(Quat q) {
		return q instanceof QuatD qd ? multiply(qd) : multiply(q.x(), q.y(), q.z(), q.w());
	}

	@Override
	public QuatF multiplyF(Quat q) {
		float xf = (float) x, yf = (float) y, zf = (float) z, wf = (float) w;
		float qx = q.xf(), qy = q.yf(), qz = q.zf(), qw = q.wf();
		return new QuatF(
				wf * qx + xf * qw + yf * qz - zf * qy,
				wf * qy - xf * qz + yf * qw + zf * qx,
				wf * qz + xf * qy - yf * qx + zf * qw,
				wf * qw - xf * qx - yf * qy - zf * qz
		);
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
	public QuatF rotateAxisDegreesF(Vec3 axis, float angle) {
		float f0 = FastMath.sinDegr(angle / 2.0f);
		float xf = (float) x, yf = (float) y, zf = (float) z, wf = (float) w;
		float qx = axis.xf() * f0, qy = axis.yf() * f0, qz = axis.zf() * f0;
		float qw = FastMath.cosDegr(angle / 2.0f);
		return new QuatF(
				wf * qx + xf * qw + yf * qz - zf * qy,
				wf * qy - xf * qz + yf * qw + zf * qx,
				wf * qz + xf * qy - yf * qx + zf * qw,
				wf * qw - xf * qx - yf * qy - zf * qz
		);
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
	public QuatF spinF(Vec3 spin) {
		float angle = spin.lengthF();
		if (angle < 1E-7f) return new QuatF((float) x, (float) y, (float) z, (float) w);
		float f0 = FastMath.sinRad(angle / 2.0f);
		float qx = spin.xf() * f0 / angle;
		float qy = spin.yf() * f0 / angle;
		float qz = spin.zf() * f0 / angle;
		float qw = FastMath.cosRad(angle / 2.0f);
		float xf = (float) x, yf = (float) y, zf = (float) z, wf = (float) w;
		return new QuatF(
				wf * qx + xf * qw + yf * qz - zf * qy,
				wf * qy - xf * qz + yf * qw + zf * qx,
				wf * qz + xf * qy - yf * qx + zf * qw,
				wf * qw - xf * qx - yf * qy - zf * qz
		);
	}

	@Override
	public QuatD conjugate() {
		return new QuatD(-x, -y, -z, w);
	}

	@Override
	public QuatF conjugateF() {
		return new QuatF((float) -x, (float) -y, (float) -z, (float) w);
	}

	@Override
	public QuatD normalize() {
		double g = FastMath.invSqrt(x * x + y * y + z * z + w * w + Double.MIN_NORMAL);
		return new QuatD(x * g, y * g, z * g, w * g);
	}

	@Override
	public QuatF normalizeF() {
		float xf = (float) x;
		float yf = (float) y;
		float zf = (float) z;
		float wf = (float) w;
		float g = FastMath.invSqrt(xf * xf + yf * yf + zf * zf + wf * wf + Float.MIN_NORMAL);
		return new QuatF(xf * g, yf * g, zf * g, wf * g);
	}

	@Override
	public Vec3D up() {
		return new Vec3D(
				2.0 * (x * y - z * w),
				1.0 - 2.0 * x * x - 2.0 * z * z,
				2.0 * (y * z + x * w)
		);
	}

	@Override
	public Vec3F upF() {
		float xf = (float) x;
		float yf = (float) y;
		float zf = (float) z;
		float wf = (float) w;
		return new Vec3F(
				2.0f * (xf * yf - zf * wf),
				1.0f - 2.0f * xf * xf - 2.0f * zf * zf,
				2.0f * (yf * zf + xf * wf)
		);
	}

	@Override
	public Vec3D left() {
		return new Vec3D(
				1.0 - 2.0 * y * y - 2.0 * z * z,
				2.0 * (x * y + z * w),
				2.0 * (z * x - y * w)
		);
	}

	@Override
	public Vec3F leftF() {
		float xf = (float) x;
		float yf = (float) y;
		float zf = (float) z;
		float wf = (float) w;
		return new Vec3F(
				1.0f - 2.0f * yf * yf - 2.0f * zf * zf,
				2.0f * (xf * yf + zf * wf),
				2.0f * (zf * xf - yf * wf)
		);
	}

	@Override
	public Vec3D forward() {
		return new Vec3D(2.0 * (z * x + y * w), 2.0 * (y * z - x * w), 1.0 - 2.0 * x * x - 2.0 * y * y);
	}

	@Override
	public Vec3F forwardF() {
		float xf = (float) x, yf = (float) y, zf = (float) z, wf = (float) w;
		return new Vec3F(
				2.0f * (zf * xf + yf * wf),
				2.0f * (yf * zf - xf * wf),
				1.0f - 2.0f * xf * xf - 2.0f * yf * yf
		);
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
