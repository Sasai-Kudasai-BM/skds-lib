package net.skds.lib2.mat.matrix3;

import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.utils.linkiges.Obj2DoublePairRecord;
import net.skds.lib2.utils.linkiges.Obj2FloatPairRecord;


public record Matrix3D(
		double m00, double m01, double m02,
		double m10, double m11, double m12,
		double m20, double m21, double m22
) implements Matrix3 {

	public static final Matrix3D SINGLE = new Matrix3D(1, 0, 0, 0, 1, 0, 0, 0, 1);

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Matrix3 m) {
			return Matrix3.valueEquals(this, m);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int i = this.m00 != 0.0 ? Double.hashCode(this.m00) : 0;
		i = 31 * i + (this.m01 != 0.0 ? Double.hashCode(this.m01) : 0);
		i = 31 * i + (this.m02 != 0.0 ? Double.hashCode(this.m02) : 0);
		i = 31 * i + (this.m10 != 0.0 ? Double.hashCode(this.m10) : 0);
		i = 31 * i + (this.m11 != 0.0 ? Double.hashCode(this.m11) : 0);
		i = 31 * i + (this.m12 != 0.0 ? Double.hashCode(this.m12) : 0);
		i = 31 * i + (this.m20 != 0.0 ? Double.hashCode(this.m20) : 0);
		i = 31 * i + (this.m21 != 0.0 ? Double.hashCode(this.m21) : 0);
		return 31 * i + (this.m22 != 0.0 ? Double.hashCode(this.m22) : 0);
	}

	@Override
	public float m00f() {
		return (float) m00;
	}

	@Override
	public float m01f() {
		return (float) m01;
	}

	@Override
	public float m02f() {
		return (float) m02;
	}

	@Override
	public float m10f() {
		return (float) m10;
	}

	@Override
	public float m11f() {
		return (float) m11;
	}

	@Override
	public float m12f() {
		return (float) m12;
	}

	@Override
	public float m20f() {
		return (float) m20;
	}

	@Override
	public float m21f() {
		return (float) m21;
	}

	@Override
	public float m22f() {
		return (float) m22;
	}

	@Override
	public Matrix3D transpose() {
		return new Matrix3D(
				m00, m10, m20,
				m01, m11, m21,
				m02, m12, m22
		);
	}

	@Override
	public Matrix3F transposeF() {
		return new Matrix3F(
				m00f(), m10f(), m20f(),
				m01f(), m11f(), m21f(),
				m02f(), m12f(), m22f()
		);
	}

	@Override
	public Matrix3D invert() {
		double f = this.m11() * this.m22() - this.m12() * this.m21();
		double f1 = -(this.m10() * this.m22() - this.m12() * this.m20());
		double f2 = this.m10() * this.m21() - this.m11() * this.m20();
		double f3 = -(this.m01() * this.m22() - this.m02() * this.m21());
		double f4 = this.m00() * this.m22() - this.m02() * this.m20();
		double f5 = -(this.m00() * this.m21() - this.m01() * this.m20());
		double f6 = this.m01() * this.m12() - this.m02() * this.m11();
		double f7 = -(this.m00() * this.m12() - this.m02() * this.m10());
		double f8 = this.m00() * this.m11() - this.m01() * this.m10();
		double det = this.m00() * f + this.m01() * f1 + this.m02() * f2;
		double invDet = 1 / det;
		return new Matrix3D(
				f * invDet,
				f1 * invDet,
				f2 * invDet,
				f3 * invDet,
				f4 * invDet,
				f5 * invDet,
				f6 * invDet,
				f7 * invDet,
				f8 * invDet
		);
	}

	@Override
	public Matrix3F invertF() {
		float f = this.m11f() * this.m22f() - this.m12f() * this.m21f();
		float f1 = -(this.m10f() * this.m22f() - this.m12f() * this.m20f());
		float f2 = this.m10f() * this.m21f() - this.m11f() * this.m20f();
		float f3 = -(this.m01f() * this.m22f() - this.m02f() * this.m21f());
		float f4 = this.m00f() * this.m22f() - this.m02f() * this.m20f();
		float f5 = -(this.m00f() * this.m21f() - this.m01f() * this.m20f());
		float f6 = this.m01f() * this.m12f() - this.m02f() * this.m11f();
		float f7 = -(this.m00f() * this.m12f() - this.m02f() * this.m10f());
		float f8 = this.m00f() * this.m11f() - this.m01f() * this.m10f();
		float det = this.m00f() * f + this.m01f() * f1 + this.m02f() * f2;
		float invDet = 1 / det;
		return new Matrix3F(
				f * invDet,
				f1 * invDet,
				f2 * invDet,
				f3 * invDet,
				f4 * invDet,
				f5 * invDet,
				f6 * invDet,
				f7 * invDet,
				f8 * invDet
		);
	}

	@Override
	public Obj2DoublePairRecord<Matrix3D> adjugateAndDet() {
		double f0 = this.m11() * this.m22() - this.m12() * this.m21();
		double f1 = -(this.m10() * this.m22() - this.m12() * this.m20());
		double f2 = this.m10() * this.m21() - this.m11() * this.m20();
		double f3 = -(this.m01() * this.m22() - this.m02() * this.m21());
		double f4 = this.m00() * this.m22() - this.m02() * this.m20();
		double f5 = -(this.m00() * this.m21() - this.m01() * this.m20());
		double f6 = this.m01() * this.m12() - this.m02() * this.m11();
		double f7 = -(this.m00() * this.m12() - this.m02() * this.m10());
		double f8 = this.m00() * this.m11() - this.m01() * this.m10();
		double det = this.m00() * f0 + this.m01() * f1 + this.m02() * f2;
		Matrix3D m = new Matrix3D(
				f0,
				f1,
				f2,
				f3,
				f4,
				f5,
				f6,
				f7,
				f8
		);
		return new Obj2DoublePairRecord<>(m, det);
	}

	@Override
	public Obj2FloatPairRecord<Matrix3F> adjugateAndDetF() {
		float f = this.m11f() * this.m22f() - this.m12f() * this.m21f();
		float f1 = -(this.m10f() * this.m22f() - this.m12f() * this.m20f());
		float f2 = this.m10f() * this.m21f() - this.m11f() * this.m20f();
		float f3 = -(this.m01f() * this.m22f() - this.m02f() * this.m21f());
		float f4 = this.m00f() * this.m22f() - this.m02f() * this.m20f();
		float f5 = -(this.m00f() * this.m21f() - this.m01f() * this.m20f());
		float f6 = this.m01f() * this.m12f() - this.m02f() * this.m11f();
		float f7 = -(this.m00f() * this.m12f() - this.m02f() * this.m10f());
		float f8 = this.m00f() * this.m11f() - this.m01f() * this.m10f();
		float det = this.m00f() * f + this.m01f() * f1 + this.m02f() * f2;
		Matrix3F m = new Matrix3F(
				f,
				f1,
				f2,
				f3,
				f4,
				f5,
				f6,
				f7,
				f8
		);
		return new Obj2FloatPairRecord<>(m, det);
	}


	@Override
	public double det() {
		double f = this.m11() * this.m22() - this.m12() * this.m21();
		double f1 = -(this.m10() * this.m22() - this.m12() * this.m20());
		double f2 = this.m10() * this.m21() - this.m11() * this.m20();
		return this.m00() * f + this.m01() * f1 + this.m02() * f2;
	}


	@Override
	public float detF() {
		float f = this.m11f() * this.m22f() - this.m12f() * this.m21f();
		float f1 = -(this.m10f() * this.m22f() - this.m12f() * this.m20f());
		float f2 = this.m10f() * this.m21f() - this.m11f() * this.m20f();
		return this.m00f() * f + this.m01f() * f1 + this.m02f() * f2;
	}


	@Override
	public Matrix3D multiply(Matrix3 m2) {
		return new Matrix3D(
				this.m00() * m2.m00() + this.m01() * m2.m10() + this.m02() * m2.m20(),
				this.m00() * m2.m01() + this.m01() * m2.m11() + this.m02() * m2.m21(),
				this.m00() * m2.m02() + this.m01() * m2.m12() + this.m02() * m2.m22(),
				this.m10() * m2.m00() + this.m11() * m2.m10() + this.m12() * m2.m20(),
				this.m10() * m2.m01() + this.m11() * m2.m11() + this.m12() * m2.m21(),
				this.m10() * m2.m02() + this.m11() * m2.m12() + this.m12() * m2.m22(),
				this.m20() * m2.m00() + this.m21() * m2.m10() + this.m22() * m2.m20(),
				this.m20() * m2.m01() + this.m21() * m2.m11() + this.m22() * m2.m21(),
				this.m20() * m2.m02() + this.m21() * m2.m12() + this.m22() * m2.m22()
		);
	}


	@Override
	public Matrix3F multiplyF(Matrix3 m2) {
		return new Matrix3F(
				this.m00f() * m2.m00f() + this.m01f() * m2.m10f() + this.m02f() * m2.m20f(),
				this.m00f() * m2.m01f() + this.m01f() * m2.m11f() + this.m02f() * m2.m21f(),
				this.m00f() * m2.m02f() + this.m01f() * m2.m12f() + this.m02f() * m2.m22f(),
				this.m10f() * m2.m00f() + this.m11f() * m2.m10f() + this.m12f() * m2.m20f(),
				this.m10f() * m2.m01f() + this.m11f() * m2.m11f() + this.m12f() * m2.m21f(),
				this.m10f() * m2.m02f() + this.m11f() * m2.m12f() + this.m12f() * m2.m22f(),
				this.m20f() * m2.m00f() + this.m21f() * m2.m10f() + this.m22f() * m2.m20f(),
				this.m20f() * m2.m01f() + this.m21f() * m2.m11f() + this.m22f() * m2.m21f(),
				this.m20f() * m2.m02f() + this.m21f() * m2.m12f() + this.m22f() * m2.m22f()
		);
	}


	@Override
	public Matrix3D multiply(Quat q) {
		double x = q.x();
		double y = q.y();
		double z = q.z();
		double w = q.w();
		double xx = 2.0 * x * x;
		double yy = 2.0 * y * y;
		double zz = 2.0 * z * z;
		double xy = 2.0 * x * y;
		double yz = 2.0 * y * z;
		double zx = 2.0 * z * x;
		double xw = 2.0 * x * w;
		double yw = 2.0 * y * w;
		double zw = 2.0 * z * w;

		double r00 = 1.0 - yy - zz;
		double r01 = xy - zw;
		double r02 = zx + yw;
		double r10 = xy + zw;
		double r11 = 1.0 - zz - xx;
		double r12 = yz - xw;
		double r20 = zx - yw;
		double r21 = yz + xw;
		double r22 = 1.0 - xx - yy;

		return new Matrix3D(
				m00 * r00 + m01 * r10 + m02 * r20,
				m00 * r01 + m01 * r11 + m02 * r21,
				m00 * r02 + m01 * r12 + m02 * r22,
				m10 * r00 + m11 * r10 + m12 * r20,
				m10 * r01 + m11 * r11 + m12 * r21,
				m10 * r02 + m11 * r12 + m12 * r22,
				m20 * r00 + m21 * r10 + m22 * r20,
				m20 * r01 + m21 * r11 + m22 * r21,
				m20 * r02 + m21 * r12 + m22 * r22
		);
	}


	@Override
	public Matrix3F multiplyF(Quat q) {
		float x = q.xf();
		float y = q.yf();
		float z = q.zf();
		float w = q.wf();
		float xx = 2.0f * x * x;
		float yy = 2.0f * y * y;
		float zz = 2.0f * z * z;
		float xy = 2.0f * x * y;
		float yz = 2.0f * y * z;
		float zx = 2.0f * z * x;
		float xw = 2.0f * x * w;
		float yw = 2.0f * y * w;
		float zw = 2.0f * z * w;

		float r00 = 1.0f - yy - zz;
		float r01 = xy - zw;
		float r02 = zx + yw;
		float r10 = xy + zw;
		float r11 = 1.0f - zz - xx;
		float r12 = yz - xw;
		float r20 = zx - yw;
		float r21 = yz + xw;
		float r22 = 1.0f - xx - yy;

		return new Matrix3F(
				(float) m00 * r00 + (float) m01 * r10 + (float) m02 * r20,
				(float) m00 * r01 + (float) m01 * r11 + (float) m02 * r21,
				(float) m00 * r02 + (float) m01 * r12 + (float) m02 * r22,
				(float) m10 * r00 + (float) m11 * r10 + (float) m12 * r20,
				(float) m10 * r01 + (float) m11 * r11 + (float) m12 * r21,
				(float) m10 * r02 + (float) m11 * r12 + (float) m12 * r22,
				(float) m20 * r00 + (float) m21 * r10 + (float) m22 * r20,
				(float) m20 * r01 + (float) m21 * r11 + (float) m22 * r21,
				(float) m20 * r02 + (float) m21 * r12 + (float) m22 * r22
		);
	}


	@Override
	public Matrix3D scale(double scale) {
		return new Matrix3D(
				m00() * scale,
				m01() * scale,
				m02() * scale,
				m10() * scale,
				m11() * scale,
				m12() * scale,
				m20() * scale,
				m21() * scale,
				m22() * scale
		);
	}


	@Override
	public Matrix3D scaleNS(double sx, double sy, double sz) {
		return new Matrix3D(
				m00() * sx,
				m01() * sy,
				m02() * sz,
				m10() * sx,
				m11() * sy,
				m12() * sz,
				m20() * sx,
				m21() * sy,
				m22() * sz
		);
	}


	@Override
	public Matrix3F scaleF(float scale) {
		return new Matrix3F(
				m00f() * scale,
				m01f() * scale,
				m02f() * scale,
				m10f() * scale,
				m11f() * scale,
				m12f() * scale,
				m20f() * scale,
				m21f() * scale,
				m22f() * scale
		);
	}


	@Override
	public Matrix3F scaleNSF(float sx, float sy, float sz) {
		return new Matrix3F(
				m00f() * sx,
				m01f() * sy,
				m02f() * sz,
				m10f() * sx,
				m11f() * sy,
				m12f() * sz,
				m20f() * sx,
				m21f() * sy,
				m22f() * sz
		);
	}


	@Override
	public Vec3D getYPRAngles() {
		double yaw;
		double pitch;
		double roll;
		// Assuming the angles are in radians.
		if (m10() > 0.999999) { // singularity at north pole
			yaw = Math.atan2(m02(), m22());
			pitch = Math.PI / 2;
			roll = 0;
		} else if (m10() < -0.999999) { // singularity at south pole
			yaw = Math.atan2(m02(), m22());
			pitch = -Math.PI / 2;
			roll = 0;
		} else {
			yaw = Math.atan2(-m20(), m00());
			pitch = Math.atan2(-m12(), m11());
			roll = Math.asin(m10());
		}

		return new Vec3D(yaw, pitch, roll);
	}


	@Override
	public Vec3D[] asNormals() {
		Vec3D[] norms = new Vec3D[3];
		norms[0] = new Vec3D(m00(), m10(), m20());
		norms[1] = new Vec3D(m01(), m11(), m21());
		norms[2] = new Vec3D(m02(), m12(), m22());
		return norms;
	}


	@Override
	public Vec3F[] asNormalsF() {
		Vec3F[] norms = new Vec3F[3];
		norms[0] = new Vec3F(m00f(), m10f(), m20f());
		norms[1] = new Vec3F(m01f(), m11f(), m21f());
		norms[2] = new Vec3F(m02f(), m12f(), m22f());
		return norms;
	}


	@Override
	public Vec3D left() {
		return new Vec3D(m00(), m10(), m20());
	}


	@Override
	public Vec3D leftNorm() {
		return Vec3.normalized(m00(), m10(), m20());
	}


	@Override
	public Vec3F leftF() {
		return new Vec3F(m00f(), m10f(), m20f());
	}


	@Override
	public Vec3F leftNormF() {
		return Vec3.normalizedF(m00f(), m10f(), m20f());
	}


	@Override
	public Vec3D up() {
		return new Vec3D(m01(), m11(), m21());
	}


	@Override
	public Vec3D upNorm() {
		return Vec3.normalized(m01(), m11(), m21());
	}


	@Override
	public Vec3F upF() {
		return new Vec3F(m01f(), m11f(), m21f());
	}


	@Override
	public Vec3F upNormF() {
		return Vec3.normalizedF(m01f(), m11f(), m21f());
	}


	@Override
	public Vec3D forward() {
		return new Vec3D(m02(), m12(), m22());
	}


	@Override
	public Vec3D forwardNorm() {
		return Vec3.normalized(m02(), m12(), m22());
	}


	@Override
	public Vec3F forwardF() {
		return new Vec3F(m02f(), m12f(), m22f());
	}


	@Override
	public Vec3F forwardNormF() {
		return Vec3.normalizedF(m02f(), m12f(), m22f());
	}


	public Matrix3D multiply(Matrix3D m2) {
		return new Matrix3D(
				m00 * m2.m00 + m01 * m2.m10 + m02 * m2.m20,
				m00 * m2.m01 + m01 * m2.m11 + m02 * m2.m21,
				m00 * m2.m02 + m01 * m2.m12 + m02 * m2.m22,
				m10 * m2.m00 + m11 * m2.m10 + m12 * m2.m20,
				m10 * m2.m01 + m11 * m2.m11 + m12 * m2.m21,
				m10 * m2.m02 + m11 * m2.m12 + m12 * m2.m22,
				m20 * m2.m00 + m21 * m2.m10 + m22 * m2.m20,
				m20 * m2.m01 + m21 * m2.m11 + m22 * m2.m21,
				m20 * m2.m02 + m21 * m2.m12 + m22 * m2.m22);
	}
}
