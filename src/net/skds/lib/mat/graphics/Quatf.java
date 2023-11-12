package net.skds.lib.mat.graphics;

import net.skds.lib.mat.Quat;

public class Quatf {
	public static final Quatf ONE = new Quatf(0.0F, 0.0F, 0.0F, 1.0F);
	public float x;
	public float y;
	public float z;
	public float w;

	public Quatf(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quatf(float[] arr) {
		if (arr == null || arr.length < 4) {
			this.w = 1;
			return;
		}
		this.x = arr[0];
		this.y = arr[1];
		this.z = arr[2];
		this.w = arr[3];
	}

	public Quatf(Vec3f axis, float angle, boolean degrees) {

		if (Math.abs(angle) < 1E-30) {

			this.x = 0;
			this.y = 0;
			this.z = 0;
			this.w = 1;
			return;
		}

		if (degrees) {
			angle *= (Math.PI / 180D);
		}

		float f = (float) Math.sin(angle / 2.0F);
		this.x = axis.x * f;
		this.y = axis.y * f;
		this.z = axis.z * f;
		this.w = (float) Math.cos(angle / 2.0F);
	}

	public Quatf(Quat quaternionIn) {
		this.x = (float) quaternionIn.x;
		this.y = (float) quaternionIn.y;
		this.z = (float) quaternionIn.z;
		this.w = (float) quaternionIn.w;
	}

	public Quatf(Quatf quaternionIn) {
		this.x = quaternionIn.x;
		this.y = quaternionIn.y;
		this.z = quaternionIn.z;
		this.w = quaternionIn.w;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
			Quatf QuaternionC = (Quatf) p_equals_1_;
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
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("Quat[").append(this.w).append(" + ");
		stringbuilder.append(this.x).append("i + ");
		stringbuilder.append(this.y).append("j + ");
		stringbuilder.append(this.z).append("k]");
		return stringbuilder.toString();
	}

	public Quatf multiplyQ(float qx, float qy, float qz, float qw) {
		float f = this.x;
		float f1 = this.y;
		float f2 = this.z;
		float f3 = this.w;
		float f4 = qx;
		float f5 = qy;
		float f6 = qz;
		float f7 = qw;
		this.x = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
		this.y = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
		this.z = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
		this.w = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
		return this;
	}

	public Quatf multiply(Quatf quaternionIn) {
		float f = this.x;
		float f1 = this.y;
		float f2 = this.z;
		float f3 = this.w;
		float f4 = quaternionIn.x;
		float f5 = quaternionIn.y;
		float f6 = quaternionIn.z;
		float f7 = quaternionIn.w;
		this.x = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
		this.y = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
		this.z = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
		this.w = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
		return this;
	}

	public Quatf rotate(Vec3f axis, float angle, boolean degrees) {
		if (degrees) {
			angle *= (Math.PI / 180D);
		}

		float f0 = (float) Math.sin(angle / 2.0);
		float x = axis.x * f0;
		float y = axis.y * f0;
		float z = axis.z * f0;
		float w = (float) Math.cos(angle / 2.0);

		float f = this.x;
		float f1 = this.y;
		float f2 = this.z;
		float f3 = this.w;

		this.x = f3 * x + f * w + f1 * z - f2 * y;
		this.y = f3 * y - f * z + f1 * w + f2 * x;
		this.z = f3 * z + f * y - f1 * x + f2 * w;
		this.w = f3 * w - f * x - f1 * y - f2 * z;
		return this;
	}

	public Quatf multiply(float valueIn) {
		this.x *= valueIn;
		this.y *= valueIn;
		this.z *= valueIn;
		this.w *= valueIn;
		return this;
	}

	public Quatf multiply(float sx, float sy, float sz, float sw) {
		this.x *= sx;
		this.y *= sy;
		this.z *= sz;
		this.w *= sw;
		return this;
	}

	public Quatf conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
		return this;
	}

	public Quatf normalize() {
		float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
		if (f > 1.0E-30) {
			//float g = MathHelper.fastInverseSqrt(f);
			float g = (float) Math.sqrt(1D / f);
			this.x *= g;
			this.y *= g;
			this.z *= g;
			this.w *= g;
		} else {
			this.x = 0.0f;
			this.y = 0.0f;
			this.z = 0.0f;
			this.w = 1.0f;
		}
		return this;
	}

	public Quatf set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	public Quatf set(Quatf q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
		return this;
	}

	public Quatf copy() {
		return new Quatf(this);
	}

	public static Quatf slerp(Quatf qa, Quatf qb, float t) {
		// quaternion to return
		Quatf qm = new Quatf(0, 0, 0, 1);
		// Calculate angle between them.
		float cosHalfTheta = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;
		// if qa=qb or qa=-qb then theta = 0 and we can return qa
		if (Math.abs(cosHalfTheta) >= 1.0) {
			qm.w = qa.w;
			qm.x = qa.x;
			qm.y = qa.y;
			qm.z = qa.z;
			return qm;
		}
		// Calculate temporary values.
		float halfTheta = (float) Math.acos(cosHalfTheta);
		float sinHalfTheta = (float) Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);
		// if theta = 180 degrees then result is not fully defined
		// we could rotate around any axis normal to qa or qb
		if (Math.abs(sinHalfTheta) < 1E-15) { // fabs is floating point absolute
			qm.w = (qa.w * 0.5f + qb.w * 0.5f);
			qm.x = (qa.x * 0.5f + qb.x * 0.5f);
			qm.y = (qa.y * 0.5f + qb.y * 0.5f);
			qm.z = (qa.z * 0.5f + qb.z * 0.5f);
			return qm;
		}
		float ratioA = (float) Math.sin((1 - t) * halfTheta) / sinHalfTheta;
		float ratioB = (float) Math.sin(t * halfTheta) / sinHalfTheta;
		//calculate Quaternion.
		qm.w = (qa.w * ratioA + qb.w * ratioB);
		qm.x = (qa.x * ratioA + qb.x * ratioB);
		qm.y = (qa.y * ratioA + qb.y * ratioB);
		qm.z = (qa.z * ratioA + qb.z * ratioB);
		return qm;
	}

	public float[] toYP() {

		float f4 = 2.f * x * x;
		float f5 = 2.f * y * y;
		float f6 = 2.f * z * z;

		float m00 = 1.0f - f5 - f6;
		float m11 = 1.0f - f6 - f4;

		float f8 = y * z;
		float f9 = z * x;
		float f10 = x * w;
		float f11 = y * w;

		float m20 = 2.0f * (f9 - f11);
		float m12 = 2.0f * (f8 - f10);

		float yaw = (float) Math.atan2(-m20, m00);
		float pitch = (float) Math.atan2(-m12, m11);

		return new float[]{yaw, pitch};
	}

	public Vec3f forward() {
		float f4 = 2.0f * x * x;
		float f5 = 2.0f * y * y;
		float f8 = y * z;
		float f9 = z * x;
		float f10 = x * w;
		float f11 = y * w;
		float vx = 2.0f * (f9 + f11);
		float vy = 2.0f * (f8 - f10);
		float vz = 1.0f - f4 - f5;

		//return new Matrix3(this).asNormals()[2];
		//return new Vec3f(-vx, vy, vz);
		return new Vec3f(vx, vy, vz);
	}

}