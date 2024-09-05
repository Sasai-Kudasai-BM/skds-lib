package net.skds.lib.mat;

import net.skds.lib.mat.graphics.Quatf;

public class Quat {
	public static final Quat ONE = new Quat(0.0F, 0.0F, 0.0F, 1.0F);
	public double x;
	public double y;
	public double z;
	public double w;

	public Quat() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 1;
	}

	public Quat(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quat(Vec3 axis, double angle, boolean degrees) {

		if (Math.abs(angle) < 1E-30) {

			this.x = 0;
			this.y = 0;
			this.z = 0;
			this.w = 1;
			return;
		}

		if (!degrees) {
			angle *= FastMath.RAD_2_DEGREES;
		}

		double f = FastMath.sinDegr(angle / 2.0F);
		this.x = axis.x * f;
		this.y = axis.y * f;
		this.z = axis.z * f;
		this.w = FastMath.cosDegr(angle / 2.0F);
	}

	public Quat(Quat quaternionIn) {
		this.x = quaternionIn.x;
		this.y = quaternionIn.y;
		this.z = quaternionIn.z;
		this.w = quaternionIn.w;
	}

	public Quat(Matrix3 m) {
		//this.w = Math.sqrt(1.0 + m1.m00 + m1.m11 + m1.m22) / 2.0;
		//double w4 = (4.0 * w);
		//this.x = (m1.m21 - m1.m12) / w4;
		//this.y = (m1.m02 - m1.m20) / w4;
		//this.z = (m1.m10 - m1.m01) / w4;

		double tr = m.m00 + m.m11 + m.m22;

		if (tr > 0) {
			double s = Math.sqrt(tr + 1.0) * 2; // S=4*qw 
			this.w = 0.25 * s;
			this.x = (m.m21 - m.m12) / s;
			this.y = (m.m02 - m.m20) / s;
			this.z = (m.m10 - m.m01) / s;
		} else if ((m.m00 > m.m11) & (m.m00 > m.m22)) {
			double s = Math.sqrt(1.0 + m.m00 - m.m11 - m.m22) * 2; // S=4*qx 
			this.w = (m.m21 - m.m12) / s;
			this.x = 0.25 * s;
			this.y = (m.m01 + m.m10) / s;
			this.z = (m.m02 + m.m20) / s;
		} else if (m.m11 > m.m22) {
			double s = Math.sqrt(1.0 + m.m11 - m.m00 - m.m22) * 2; // S=4*qy
			this.w = (m.m02 - m.m20) / s;
			this.x = (m.m01 + m.m10) / s;
			this.y = 0.25 * s;
			this.z = (m.m12 + m.m21) / s;
		} else {
			double s = Math.sqrt(1.0 + m.m22 - m.m00 - m.m11) * 2; // S=4*qz
			this.w = (m.m10 - m.m01) / s;
			this.x = (m.m02 + m.m20) / s;
			this.y = (m.m12 + m.m21) / s;
			this.z = 0.25 * s;
		}
	}

	public Quat rotate(Vec3 spin) {
		double angle = spin.length();
		if (angle < 1E-7) {
			return this;
		}

		double axisX = spin.x / angle;
		double axisY = spin.y / angle;
		double axisZ = spin.z / angle;

		double f0 = FastMath.sinRad(angle / 2.0);
		double f = axisX * f0;
		double f1 = axisY * f0;
		double f2 = axisZ * f0;
		double f3 = FastMath.cosRad(angle / 2.0);

		double x = this.x;
		double y = this.y;
		double z = this.z;
		double w = this.w;

		this.x = f3 * x + f * w + f1 * z - f2 * y;
		this.y = f3 * y - f * z + f1 * w + f2 * x;
		this.z = f3 * z + f * y - f1 * x + f2 * w;
		this.w = f3 * w - f * x - f1 * y - f2 * z;
		return this;
	}

	public static Quat fromForward(Vec3 forward) {
		return new Quat(Matrix3.fromForward(forward));
	}

	public Quatf toFloatQ() {
		return new Quatf((float) x, (float) y, (float) z, (float) w);
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
			Quat QuaternionC = (Quat) p_equals_1_;
			if (Double.compare(QuaternionC.x, this.x) != 0) {
				return false;
			} else if (Double.compare(QuaternionC.y, this.y) != 0) {
				return false;
			} else if (Double.compare(QuaternionC.z, this.z) != 0) {
				return false;
			} else {
				return Double.compare(QuaternionC.w, this.w) == 0;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = Float.floatToIntBits((float) this.x);
		i = 31 * i + Float.floatToIntBits((float) this.y);
		i = 31 * i + Float.floatToIntBits((float) this.z);
		return 31 * i + Float.floatToIntBits((float) this.w);
	}

	public String toString() {
		String stringBuilder = "Quat[" + this.w + " + " +
				this.x + "i + " +
				this.y + "j + " +
				this.z + "k]";
		return stringBuilder;
	}

	public Quat multiplyQ(double qx, double qy, double qz, double qw) {
		double x2 = qx;
		double y2 = qy;
		double z2 = qz;
		double w2 = qw;
		this.x = w * x2 + x * w2 + y * z2 - z * y2;
		this.y = w * y2 - x * z2 + y * w2 + z * x2;
		this.z = w * z2 + x * y2 - y * x2 + z * w2;
		this.w = w * w2 - x * x2 - y * y2 - z * z2;
		return this;
	}

	public Quat multiply(Quat quaternionIn) {
		double f = this.x;
		double f1 = this.y;
		double f2 = this.z;
		double f3 = this.w;
		double f4 = quaternionIn.x;
		double f5 = quaternionIn.y;
		double f6 = quaternionIn.z;
		double f7 = quaternionIn.w;
		this.x = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
		this.y = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
		this.z = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
		this.w = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
		return this;
	}

	public Quat rotate(Vec3 axis, double angle, boolean degrees) {
		if (!degrees) {
			angle *= FastMath.RAD_2_DEGREES;
		}

		double f0 = FastMath.sinDegr(angle / 2.0);
		double x = axis.x * f0;
		double y = axis.y * f0;
		double z = axis.z * f0;
		double w = FastMath.cosDegr(angle / 2.0);

		double f = this.x;
		double f1 = this.y;
		double f2 = this.z;
		double f3 = this.w;

		this.x = f3 * x + f * w + f1 * z - f2 * y;
		this.y = f3 * y - f * z + f1 * w + f2 * x;
		this.z = f3 * z + f * y - f1 * x + f2 * w;
		this.w = f3 * w - f * x - f1 * y - f2 * z;
		return this;
	}

	public Quat multiply(double valueIn) {
		this.x *= valueIn;
		this.y *= valueIn;
		this.z *= valueIn;
		this.w *= valueIn;
		return this;
	}

	public Quat multiply(double sx, double sy, double sz, double sw) {
		this.x *= sx;
		this.y *= sy;
		this.z *= sz;
		this.w *= sw;
		return this;
	}

	public Quat conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Quat normalize() {
		double f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
		if (f > 1.0E-30) {
			double g = FastMath.invSqrt(f);
			this.x *= g;
			this.y *= g;
			this.z *= g;
			this.w *= g;
		} else {
			this.x = 0.0;
			this.y = 0.0;
			this.z = 0.0;
			this.w = 1.0;
		}
		return this;
	}

	public Quat set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public Quat set(Quat q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
		return this;
	}

	public Quat copy() {
		return new Quat(this);
	}

	public static Quat slerp(Quat qa, Quat qb, double t) {
		// quaternion to return
		Quat qm = new Quat(0, 0, 0, 1);
		// Calculate angle between them.
		double cosHalfTheta = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;
		// if qa=qb or qa=-qb then theta = 0 and we can return qa
		if (Math.abs(cosHalfTheta) >= 1.0) {
			qm.w = qa.w;
			qm.x = qa.x;
			qm.y = qa.y;
			qm.z = qa.z;
			return qm;
		}
		// Calculate temporary values.
		double halfTheta = Math.acos(cosHalfTheta);
		double sinHalfTheta = Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);
		// if theta = 180 degrees then result is not fully defined
		// we could rotate around any axis normal to qa or qb
		if (Math.abs(sinHalfTheta) < 1E-15) { // fabs is floating point absolute
			qm.w = (qa.w * 0.5 + qb.w * 0.5);
			qm.x = (qa.x * 0.5 + qb.x * 0.5);
			qm.y = (qa.y * 0.5 + qb.y * 0.5);
			qm.z = (qa.z * 0.5 + qb.z * 0.5);
			return qm;
		}
		double ratioA = FastMath.sinRad((1 - t) * halfTheta) / sinHalfTheta;
		double ratioB = FastMath.sinRad(t * halfTheta) / sinHalfTheta;
		//calculate Quaternion.
		qm.w = (qa.w * ratioA + qb.w * ratioB);
		qm.x = (qa.x * ratioA + qb.x * ratioB);
		qm.y = (qa.y * ratioA + qb.y * ratioB);
		qm.z = (qa.z * ratioA + qb.z * ratioB);
		return qm;
	}

	public double[] toYP() {

		double f4 = 2.0 * x * x;
		double f5 = 2.0 * y * y;
		double f6 = 2.0 * z * z;

		double m00 = 1.0 - f5 - f6;
		double m11 = 1.0 - f6 - f4;

		double f8 = y * z;
		double f9 = z * x;
		double f10 = x * w;
		double f11 = y * w;

		double m20 = 2.0 * (f9 - f11);
		double m12 = 2.0 * (f8 - f10);

		double yaw = Math.atan2(-m20, m00);
		double pitch = Math.atan2(-m12, m11);

		return new double[]{yaw, pitch};
	}

	public Vec3 forward() {
		double f4 = 2.0 * x * x;
		double f5 = 2.0 * y * y;
		double f8 = y * z;
		double f9 = z * x;
		double f10 = x * w;
		double f11 = y * w;
		double vx = 2.0 * (f9 + f11);
		double vy = 2.0 * (f8 - f10);
		double vz = 1.0 - f4 - f5;

		//return new Matrix3(this).asNormals()[2];
		//return new Vec3(-vx, vy, vz);
		return new Vec3(vx, vy, vz);
	}

}