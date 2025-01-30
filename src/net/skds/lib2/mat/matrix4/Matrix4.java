package net.skds.lib2.mat.matrix4;


import lombok.NoArgsConstructor;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.utils.linkiges.Obj2DoublePairRecord;
import net.skds.lib2.utils.linkiges.Obj2FloatPairRecord;

@SuppressWarnings("unused")
public sealed interface Matrix4 permits Matrix4D, Matrix4F {

	Matrix4 SINGLE = Matrix4D.SINGLE;

	double m00();

	double m01();

	double m02();

	double m03();

	double m10();

	double m11();

	double m12();

	double m13();

	double m20();

	double m21();

	double m22();

	double m23();

	double m30();

	double m31();

	double m32();

	double m33();

	default float m00f() {
		return (float) m00();
	}

	default float m01f() {
		return (float) m01();
	}

	default float m02f() {
		return (float) m02();
	}

	default float m03f() {
		return (float) m03();
	}

	default float m10f() {
		return (float) m10();
	}

	default float m11f() {
		return (float) m11();
	}

	default float m12f() {
		return (float) m12();
	}

	default float m13f() {
		return (float) m13();
	}

	default float m20f() {
		return (float) m20();
	}

	default float m21f() {
		return (float) m21();
	}

	default float m22f() {
		return (float) m22();
	}

	default float m23f() {
		return (float) m23();
	}

	default float m30f() {
		return (float) m30();
	}

	default float m31f() {
		return (float) m31();
	}

	default float m32f() {
		return (float) m32();
	}

	default float m33f() {
		return (float) m33();
	}

	static boolean equals(Matrix4 m1, Matrix4 m2) {
		if (m1 == m2) {
			return true;
		} else if ((m1 == null) != (m2 == null)) {
			return false;
		} else {
			return Double.compare(m1.m00(), m2.m00()) == 0 && Double.compare(m1.m01(), m2.m01()) == 0
					&& Double.compare(m1.m02(), m2.m02()) == 0 && Double.compare(m1.m03(), m2.m03()) == 0
					&& Double.compare(m1.m10(), m2.m10()) == 0 && Double.compare(m1.m11(), m2.m11()) == 0
					&& Double.compare(m1.m12(), m2.m12()) == 0 && Double.compare(m1.m13(), m2.m13()) == 0
					&& Double.compare(m1.m20(), m2.m20()) == 0 && Double.compare(m1.m21(), m2.m21()) == 0
					&& Double.compare(m1.m22(), m2.m22()) == 0 && Double.compare(m1.m23(), m2.m23()) == 0
					&& Double.compare(m1.m30(), m2.m30()) == 0 && Double.compare(m1.m31(), m2.m31()) == 0
					&& Double.compare(m1.m32(), m2.m32()) == 0 && Double.compare(m1.m33(), m2.m33()) == 0
					;
		}
	}

	static Matrix4D fromQuat(Quat q) {
		double f = q.x();
		double f1 = q.y();
		double f2 = q.z();
		double f3 = q.w();
		double f4 = 2.0F * f * f;
		double f5 = 2.0F * f1 * f1;
		double f6 = 2.0F * f2 * f2;
		double m00 = 1.0F - f5 - f6;
		double m11 = 1.0F - f6 - f4;
		double m22 = 1.0F - f4 - f5;
		double m33 = 1.0F;
		double f7 = f * f1;
		double f8 = f1 * f2;
		double f9 = f2 * f;
		double f10 = f * f3;
		double f11 = f1 * f3;
		double f12 = f2 * f3;
		double m10 = 2.0F * (f7 + f12);
		double m01 = 2.0F * (f7 - f12);
		double m20 = 2.0F * (f9 - f11);
		double m02 = 2.0F * (f9 + f11);
		double m21 = 2.0F * (f8 + f10);
		double m12 = 2.0F * (f8 - f10);

		return new Matrix4D(
				m00, m01, m02, 0,
				m10, m11, m12, 0,
				m20, m21, m22, 0,
				0, 0, 0, 1
		);
	}

	static Matrix4F fromQuatF(Quat q) {
		float f = q.xf();
		float f1 = q.yf();
		float f2 = q.zf();
		float f3 = q.wf();
		float f4 = 2.0F * f * f;
		float f5 = 2.0F * f1 * f1;
		float f6 = 2.0F * f2 * f2;
		float m00 = 1.0F - f5 - f6;
		float m11 = 1.0F - f6 - f4;
		float m22 = 1.0F - f4 - f5;
		float m33 = 1.0F;
		float f7 = f * f1;
		float f8 = f1 * f2;
		float f9 = f2 * f;
		float f10 = f * f3;
		float f11 = f1 * f3;
		float f12 = f2 * f3;
		float m10 = 2.0F * (f7 + f12);
		float m01 = 2.0F * (f7 - f12);
		float m20 = 2.0F * (f9 - f11);
		float m02 = 2.0F * (f9 + f11);
		float m21 = 2.0F * (f8 + f10);
		float m12 = 2.0F * (f8 - f10);

		return new Matrix4F(
				m00, m01, m02, 0,
				m10, m11, m12, 0,
				m20, m21, m22, 0,
				0, 0, 0, 1
		);
	}

	static Matrix4D fromMatrix3(Matrix3 m) {
		return new Matrix4D(
				m.m00(), m.m01(), m.m02(), 0,
				m.m10(), m.m11(), m.m12(), 0,
				m.m20(), m.m21(), m.m22(), 0,
				0, 0, 0, 1
		);
	}

	static Matrix4F fromMatrix3F(Matrix3 m) {
		return new Matrix4F(
				m.m00f(), m.m01f(), m.m02f(), 0,
				m.m10f(), m.m11f(), m.m12f(), 0,
				m.m20f(), m.m21f(), m.m22f(), 0,
				0, 0, 0, 1
		);
	}

	static Matrix4D fromArray(float[] m) {
		return new Matrix4D(
				m[0], m[1], m[2], m[3],
				m[4], m[5], m[6], m[7],
				m[8], m[9], m[10], m[11],
				m[12], m[13], m[14], m[15]
		);
	}

	static Matrix4D fromArray(double[] m) {
		return new Matrix4D(
				m[0], m[1], m[2], m[3],
				m[4], m[5], m[6], m[7],
				m[8], m[9], m[10], m[11],
				m[12], m[13], m[14], m[15]
		);
	}


	static Matrix4F fromArrayF(float[] m) {
		return new Matrix4F(
				m[0], m[1], m[2], m[3],
				m[4], m[5], m[6], m[7],
				m[8], m[9], m[10], m[11],
				m[12], m[13], m[14], m[15]
		);
	}

	static Matrix4F fromArrayF(double[] m) {
		return new Matrix4F(
				(float) m[0], (float) m[1], (float) m[2], (float) m[3],
				(float) m[4], (float) m[5], (float) m[6], (float) m[7],
				(float) m[8], (float) m[9], (float) m[10], (float) m[11],
				(float) m[12], (float) m[13], (float) m[14], (float) m[15]
		);
	}

	default Obj2DoublePairRecord<Matrix4D> adjugateAndDet() {
		double f00 = this.m00() * this.m11() - this.m01() * this.m10();
		double f01 = this.m00() * this.m12() - this.m02() * this.m10();
		double f02 = this.m00() * this.m13() - this.m03() * this.m10();
		double f03 = this.m01() * this.m12() - this.m02() * this.m11();
		double f04 = this.m01() * this.m13() - this.m03() * this.m11();
		double f05 = this.m02() * this.m13() - this.m03() * this.m12();
		double f06 = this.m20() * this.m31() - this.m21() * this.m30();
		double f07 = this.m20() * this.m32() - this.m22() * this.m30();
		double f08 = this.m20() * this.m33() - this.m23() * this.m30();
		double f09 = this.m21() * this.m32() - this.m22() * this.m31();
		double f10 = this.m21() * this.m33() - this.m23() * this.m31();
		double f11 = this.m22() * this.m33() - this.m23() * this.m32();
		double f12 = this.m11() * f11 - this.m12() * f10 + this.m13() * f09;
		double f13 = -this.m10() * f11 + this.m12() * f08 - this.m13() * f07;
		double f14 = this.m10() * f10 - this.m11() * f08 + this.m13() * f06;
		double f15 = -this.m10() * f09 + this.m11() * f07 - this.m12() * f06;
		double f16 = -this.m01() * f11 + this.m02() * f10 - this.m03() * f09;
		double f17 = this.m00() * f11 - this.m02() * f08 + this.m03() * f07;
		double f18 = -this.m00() * f10 + this.m01() * f08 - this.m03() * f06;
		double f19 = this.m00() * f09 - this.m01() * f07 + this.m02() * f06;
		double f20 = this.m31() * f05 - this.m32() * f04 + this.m33() * f03;
		double f21 = -this.m30() * f05 + this.m32() * f02 - this.m33() * f01;
		double f22 = this.m30() * f04 - this.m31() * f02 + this.m33() * f00;
		double f23 = -this.m30() * f03 + this.m31() * f01 - this.m32() * f00;
		double f24 = -this.m21() * f05 + this.m22() * f04 - this.m23() * f03;
		double f25 = this.m20() * f05 - this.m22() * f02 + this.m23() * f01;
		double f26 = -this.m20() * f04 + this.m21() * f02 - this.m23() * f00;
		double f27 = this.m20() * f03 - this.m21() * f01 + this.m22() * f00;
		Matrix4Builder matrix4 = new Matrix4Builder();

		matrix4.m00 = f12;
		matrix4.m10 = f13;
		matrix4.m20 = f14;
		matrix4.m30 = f15;
		matrix4.m01 = f16;
		matrix4.m11 = f17;
		matrix4.m21 = f18;
		matrix4.m31 = f19;
		matrix4.m02 = f20;
		matrix4.m12 = f21;
		matrix4.m22 = f22;
		matrix4.m32 = f23;
		matrix4.m03 = f24;
		matrix4.m13 = f25;
		matrix4.m23 = f26;
		matrix4.m33 = f27;

		double det = f00 * f11 - f01 * f10 + f02 * f09 + f03 * f08 - f04 * f07 + f05 * f06;
		Matrix4D m = matrix4.buildD();
		return new Obj2DoublePairRecord<>(m, det);
	}

	default Obj2FloatPairRecord<Matrix4F> adjugateAndDetF() {
		float f00 = this.m00f() * this.m11f() - this.m01f() * this.m10f();
		float f01 = this.m00f() * this.m12f() - this.m02f() * this.m10f();
		float f02 = this.m00f() * this.m13f() - this.m03f() * this.m10f();
		float f03 = this.m01f() * this.m12f() - this.m02f() * this.m11f();
		float f04 = this.m01f() * this.m13f() - this.m03f() * this.m11f();
		float f05 = this.m02f() * this.m13f() - this.m03f() * this.m12f();
		float f06 = this.m20f() * this.m31f() - this.m21f() * this.m30f();
		float f07 = this.m20f() * this.m32f() - this.m22f() * this.m30f();
		float f08 = this.m20f() * this.m33f() - this.m23f() * this.m30f();
		float f09 = this.m21f() * this.m32f() - this.m22f() * this.m31f();
		float f10 = this.m21f() * this.m33f() - this.m23f() * this.m31f();
		float f11 = this.m22f() * this.m33f() - this.m23f() * this.m32f();
		float f12 = this.m11f() * f11 - this.m12f() * f10 + this.m13f() * f09;
		float f13 = -this.m10f() * f11 + this.m12f() * f08 - this.m13f() * f07;
		float f14 = this.m10f() * f10 - this.m11f() * f08 + this.m13f() * f06;
		float f15 = -this.m10f() * f09 + this.m11f() * f07 - this.m12f() * f06;
		float f16 = -this.m01f() * f11 + this.m02f() * f10 - this.m03f() * f09;
		float f17 = this.m00f() * f11 - this.m02f() * f08 + this.m03f() * f07;
		float f18 = -this.m00f() * f10 + this.m01f() * f08 - this.m03f() * f06;
		float f19 = this.m00f() * f09 - this.m01f() * f07 + this.m02f() * f06;
		float f20 = this.m31f() * f05 - this.m32f() * f04 + this.m33f() * f03;
		float f21 = -this.m30f() * f05 + this.m32f() * f02 - this.m33f() * f01;
		float f22 = this.m30f() * f04 - this.m31f() * f02 + this.m33f() * f00;
		float f23 = -this.m30f() * f03 + this.m31f() * f01 - this.m32f() * f00;
		float f24 = -this.m21f() * f05 + this.m22f() * f04 - this.m23f() * f03;
		float f25 = this.m20f() * f05 - this.m22f() * f02 + this.m23f() * f01;
		float f26 = -this.m20f() * f04 + this.m21f() * f02 - this.m23f() * f00;
		float f27 = this.m20f() * f03 - this.m21f() * f01 + this.m22f() * f00;
		Matrix4Builder matrix4 = new Matrix4Builder();

		matrix4.m00 = f12;
		matrix4.m10 = f13;
		matrix4.m20 = f14;
		matrix4.m30 = f15;
		matrix4.m01 = f16;
		matrix4.m11 = f17;
		matrix4.m21 = f18;
		matrix4.m31 = f19;
		matrix4.m02 = f20;
		matrix4.m12 = f21;
		matrix4.m22 = f22;
		matrix4.m32 = f23;
		matrix4.m03 = f24;
		matrix4.m13 = f25;
		matrix4.m23 = f26;
		matrix4.m33 = f27;

		float det = f00 * f11 - f01 * f10 + f02 * f09 + f03 * f08 - f04 * f07 + f05 * f06;
		Matrix4F m = matrix4.buildF();
		return new Obj2FloatPairRecord<>(m, det);
	}

	default double determinant() {
		double f = this.m00() * this.m11() - this.m01() * this.m10();
		double g = this.m00() * this.m12() - this.m02() * this.m10();
		double h = this.m00() * this.m13() - this.m03() * this.m10();
		double i = this.m01() * this.m12() - this.m02() * this.m11();
		double j = this.m01() * this.m13() - this.m03() * this.m11();
		double k = this.m02() * this.m13() - this.m03() * this.m12();
		double l = this.m20() * this.m31() - this.m21() * this.m30();
		double m = this.m20() * this.m32() - this.m22() * this.m30();
		double n = this.m20() * this.m33() - this.m23() * this.m30();
		double o = this.m21() * this.m32() - this.m22() * this.m31();
		double p = this.m21() * this.m33() - this.m23() * this.m31();
		double q = this.m22() * this.m33() - this.m23() * this.m32();
		return f * q - g * p + h * o + i * n - j * m + k * l;
	}

	default Matrix4D translate(double x, double y, double z) {
		double m03 = this.m00() * x + this.m01() * y + this.m02() * z + this.m03();
		double m13 = this.m10() * x + this.m11() * y + this.m12() * z + this.m13();
		double m23 = this.m20() * x + this.m21() * y + this.m22() * z + this.m23();
		double m33 = this.m30() * x + this.m31() * y + this.m32() * z + this.m33();
		return new Matrix4D(
				this.m00(), this.m01(), this.m02(), m03,
				this.m10(), this.m11(), this.m12(), m13,
				this.m20(), this.m20(), this.m20(), m23,
				this.m30(), this.m30(), this.m30(), m33
		);
	}

	default Matrix4F translateF(float x, float y, float z) {
		float m03 = this.m00f() * x + this.m01f() * y + this.m02f() * z + this.m03f();
		float m13 = this.m10f() * x + this.m11f() * y + this.m12f() * z + this.m13f();
		float m23 = this.m20f() * x + this.m21f() * y + this.m22f() * z + this.m23f();
		float m33 = this.m30f() * x + this.m31f() * y + this.m32f() * z + this.m33f();
		return new Matrix4F(
				this.m00f(), this.m01f(), this.m02f(), m03,
				this.m10f(), this.m11f(), this.m12f(), m13,
				this.m20f(), this.m21f(), this.m22f(), m23,
				this.m30f(), this.m31f(), this.m32f(), m33
		);
	}

	default Matrix4D translate(Vec3 vec) {
		double x = vec.xf();
		double y = vec.yf();
		double z = vec.zf();
		double m03 = this.m00() * x + this.m01() * y + this.m02() * z + this.m03();
		double m13 = this.m10() * x + this.m11() * y + this.m12() * z + this.m13();
		double m23 = this.m20() * x + this.m21() * y + this.m22() * z + this.m23();
		double m33 = this.m30() * x + this.m31() * y + this.m32() * z + this.m33();
		return new Matrix4D(
				this.m00(), this.m01(), this.m02(), m03,
				this.m10(), this.m11(), this.m12(), m13,
				this.m20(), this.m21(), this.m22(), m23,
				this.m30(), this.m31(), this.m32(), m33
		);
	}

	default Matrix4F translateF(Vec3 vec) {
		float x = vec.xf();
		float y = vec.yf();
		float z = vec.zf();
		float m03 = this.m00f() * x + this.m01f() * y + this.m02f() * z + this.m03f();
		float m13 = this.m10f() * x + this.m11f() * y + this.m12f() * z + this.m13f();
		float m23 = this.m20f() * x + this.m21f() * y + this.m22f() * z + this.m23f();
		float m33 = this.m30f() * x + this.m31f() * y + this.m32f() * z + this.m33f();
		return new Matrix4F(
				this.m00f(), this.m01f(), this.m02f(), m03,
				this.m10f(), this.m11f(), this.m12f(), m13,
				this.m20f(), this.m21f(), this.m22f(), m23,
				this.m30f(), this.m31f(), this.m32f(), m33
		);
	}

	default Matrix4D translateAbsolute(double x, double y, double z) {
		return new Matrix4D(
				this.m00(), this.m01(), this.m02(), this.m03() + x,
				this.m10(), this.m11(), this.m12(), this.m13() + y,
				this.m20(), this.m21(), this.m22(), this.m23() + z,
				this.m30(), this.m31(), this.m32(), this.m33()
		);
	}

	default Matrix4F translateAbsoluteF(float x, float y, float z) {
		return new Matrix4F(
				this.m00f(), this.m01f(), this.m02f(), this.m03f() + x,
				this.m10f(), this.m11f(), this.m12f(), this.m13f() + y,
				this.m20f(), this.m21f(), this.m22f(), this.m23f() + z,
				this.m30f(), this.m31f(), this.m32f(), this.m33f()
		);
	}

	default Matrix4D translateAbsolute(Vec3 vec) {
		double x = vec.x();
		double y = vec.y();
		double z = vec.z();
		return new Matrix4D(
				this.m00(), this.m01(), this.m02(), this.m03() + vec.x(),
				this.m10(), this.m11(), this.m12(), this.m13() + vec.y(),
				this.m20(), this.m21(), this.m22(), this.m23() + vec.z(),
				this.m30(), this.m31(), this.m32(), this.m33()
		);
	}

	default Matrix4F translateAbsoluteF(Vec3 vec) {
		return new Matrix4F(
				this.m00f(), this.m01f(), this.m02f(), this.m03f() + vec.xf(),
				this.m10f(), this.m11f(), this.m12f(), this.m13f() + vec.yf(),
				this.m20f(), this.m21f(), this.m22f(), this.m23f() + vec.zf(),
				this.m30f(), this.m31f(), this.m32f(), this.m33f()
		);
	}

	default Matrix4D scale(double x, double y, double z) {
		return multiply(new Matrix4Builder().m00(x).m11(y).m22(z).buildD());
	}

	default Matrix4F scaleF(float x, float y, float z) {
		return multiplyF(new Matrix4Builder().m00(x).m11(y).m22(z).buildF());
	}

	default Matrix4D transpose() {
		return new Matrix4D(
				this.m00(), this.m10(), this.m20(), this.m30(),
				this.m01(), this.m11(), this.m21(), this.m31(),
				this.m02(), this.m12(), this.m22(), this.m32(),
				this.m03(), this.m13(), this.m23(), this.m33()
		);
	}

	default Matrix4F transposeF() {
		return new Matrix4F(
				this.m00f(), this.m10f(), this.m20f(), this.m30f(),
				this.m01f(), this.m11f(), this.m21f(), this.m31f(),
				this.m02f(), this.m12f(), this.m22f(), this.m32f(),
				this.m03f(), this.m13f(), this.m23f(), this.m33f()
		);
	}

	default Matrix4D inverse() {
		// inline adjugateAndDet
		Obj2DoublePairRecord<Matrix4D> f = adjugateAndDet();
		if (Math.abs(f.doubleValue()) > 1.0E-16F) {
			return f.objectValue().multiply(1 / f.doubleValue());
		}
		return new Matrix4Builder(this).buildD();
	}

	default Matrix4F inverseF() {
		// inline adjugateAndDet
		Obj2FloatPairRecord<Matrix4F> f = adjugateAndDetF();
		if (Math.abs(f.floatValue()) > 1.0E-16F) {
			return f.objectValue().multiplyF(1 / f.floatValue());
		}
		return new Matrix4Builder(this).buildF();
	}

	default Matrix4D multiply(Matrix4 m) {
		double f00 = this.m00() * m.m00() + this.m01() * m.m10() + this.m02() * m.m20() + this.m03() * m.m30();
		double f01 = this.m00() * m.m01() + this.m01() * m.m11() + this.m02() * m.m21() + this.m03() * m.m31();
		double f02 = this.m00() * m.m02() + this.m01() * m.m12() + this.m02() * m.m22() + this.m03() * m.m32();
		double f03 = this.m00() * m.m03() + this.m01() * m.m13() + this.m02() * m.m23() + this.m03() * m.m33();
		double f10 = this.m10() * m.m00() + this.m11() * m.m10() + this.m12() * m.m20() + this.m13() * m.m30();
		double f11 = this.m10() * m.m01() + this.m11() * m.m11() + this.m12() * m.m21() + this.m13() * m.m31();
		double f12 = this.m10() * m.m02() + this.m11() * m.m12() + this.m12() * m.m22() + this.m13() * m.m32();
		double f13 = this.m10() * m.m03() + this.m11() * m.m13() + this.m12() * m.m23() + this.m13() * m.m33();
		double f20 = this.m20() * m.m00() + this.m21() * m.m10() + this.m22() * m.m20() + this.m23() * m.m30();
		double f21 = this.m20() * m.m01() + this.m21() * m.m11() + this.m22() * m.m21() + this.m23() * m.m31();
		double f22 = this.m20() * m.m02() + this.m21() * m.m12() + this.m22() * m.m22() + this.m23() * m.m32();
		double f23 = this.m20() * m.m03() + this.m21() * m.m13() + this.m22() * m.m23() + this.m23() * m.m33();
		double f30 = this.m30() * m.m00() + this.m31() * m.m10() + this.m32() * m.m20() + this.m33() * m.m30();
		double f31 = this.m30() * m.m01() + this.m31() * m.m11() + this.m32() * m.m21() + this.m33() * m.m31();
		double f32 = this.m30() * m.m02() + this.m31() * m.m12() + this.m32() * m.m22() + this.m33() * m.m32();
		double f33 = this.m30() * m.m03() + this.m31() * m.m13() + this.m32() * m.m23() + this.m33() * m.m33();
		return new Matrix4D(
				f00, f01, f02, f03,
				f10, f11, f12, f13,
				f20, f21, f22, f23,
				f30, f31, f32, f33
		);
	}

	default Matrix4F multiplyF(Matrix4 m) {
		float f00 = this.m00f() * m.m00f() + this.m01f() * m.m10f() + this.m02f() * m.m20f() + this.m03f() * m.m30f();
		float f01 = this.m00f() * m.m01f() + this.m01f() * m.m11f() + this.m02f() * m.m21f() + this.m03f() * m.m31f();
		float f02 = this.m00f() * m.m02f() + this.m01f() * m.m12f() + this.m02f() * m.m22f() + this.m03f() * m.m32f();
		float f03 = this.m00f() * m.m03f() + this.m01f() * m.m13f() + this.m02f() * m.m23f() + this.m03f() * m.m33f();
		float f10 = this.m10f() * m.m00f() + this.m11f() * m.m10f() + this.m12f() * m.m20f() + this.m13f() * m.m30f();
		float f11 = this.m10f() * m.m01f() + this.m11f() * m.m11f() + this.m12f() * m.m21f() + this.m13f() * m.m31f();
		float f12 = this.m10f() * m.m02f() + this.m11f() * m.m12f() + this.m12f() * m.m22f() + this.m13f() * m.m32f();
		float f13 = this.m10f() * m.m03f() + this.m11f() * m.m13f() + this.m12f() * m.m23f() + this.m13f() * m.m33f();
		float f20 = this.m20f() * m.m00f() + this.m21f() * m.m10f() + this.m22f() * m.m20f() + this.m23f() * m.m30f();
		float f21 = this.m20f() * m.m01f() + this.m21f() * m.m11f() + this.m22f() * m.m21f() + this.m23f() * m.m31f();
		float f22 = this.m20f() * m.m02f() + this.m21f() * m.m12f() + this.m22f() * m.m22f() + this.m23f() * m.m32f();
		float f23 = this.m20f() * m.m03f() + this.m21f() * m.m13f() + this.m22f() * m.m23f() + this.m23f() * m.m33f();
		float f30 = this.m30f() * m.m00f() + this.m31f() * m.m10f() + this.m32f() * m.m20f() + this.m33f() * m.m30f();
		float f31 = this.m30f() * m.m01f() + this.m31f() * m.m11f() + this.m32f() * m.m21f() + this.m33f() * m.m31f();
		float f32 = this.m30f() * m.m02f() + this.m31f() * m.m12f() + this.m32f() * m.m22f() + this.m33f() * m.m32f();
		float f33 = this.m30f() * m.m03f() + this.m31f() * m.m13f() + this.m32f() * m.m23f() + this.m33f() * m.m33f();
		return new Matrix4F(
				f00, f01, f02, f03,
				f10, f11, f12, f13,
				f20, f21, f22, f23,
				f30, f31, f32, f33
		);
	}

	default Matrix4D multiply(Quat q) {
		return multiply(Matrix4.fromQuat(q));
	}

	default Matrix4F multiplyF(Quat q) {
		return multiplyF(Matrix4.fromQuat(q));
	}

	default Matrix4D multiply(double scale) {
		double m00 = this.m00() * scale;
		double m01 = this.m01() * scale;
		double m02 = this.m02() * scale;
		double m03 = this.m03() * scale;
		double m10 = this.m10() * scale;
		double m11 = this.m11() * scale;
		double m12 = this.m12() * scale;
		double m13 = this.m13() * scale;
		double m20 = this.m20() * scale;
		double m21 = this.m21() * scale;
		double m22 = this.m22() * scale;
		double m23 = this.m23() * scale;
		double m30 = this.m30() * scale;
		double m31 = this.m31() * scale;
		double m32 = this.m32() * scale;
		double m33 = this.m33() * scale;
		return new Matrix4D(
				m00, m01, m02, m03,
				m10, m11, m12, m13,
				m20, m21, m22, m23,
				m30, m31, m32, m33
		);
	}

	default Matrix4F multiplyF(float scale) {
		float m00 = this.m00f() * scale;
		float m01 = this.m01f() * scale;
		float m02 = this.m02f() * scale;
		float m03 = this.m03f() * scale;
		float m10 = this.m10f() * scale;
		float m11 = this.m11f() * scale;
		float m12 = this.m12f() * scale;
		float m13 = this.m13f() * scale;
		float m20 = this.m20f() * scale;
		float m21 = this.m21f() * scale;
		float m22 = this.m22f() * scale;
		float m23 = this.m23f() * scale;
		float m30 = this.m30f() * scale;
		float m31 = this.m31f() * scale;
		float m32 = this.m32f() * scale;
		float m33 = this.m33f() * scale;
		return new Matrix4F(
				m00, m01, m02, m03,
				m10, m11, m12, m13,
				m20, m21, m22, m23,
				m30, m31, m32, m33
		);
	}

	static Matrix4D perspective(double fov, double aspectRatio, double nearPlane, double farPlane) {
		double f = (1.0D / Math.tan(fov * Math.PI / 180F / 2.0D));
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = f / aspectRatio;
		matrix4.m11 = f;
		matrix4.m22 = (farPlane + nearPlane) / (nearPlane - farPlane);
		matrix4.m33 = 0.0F;
		matrix4.m32 = -1.0F;
		matrix4.m23 = 2.0F * farPlane * nearPlane / (nearPlane - farPlane);
		return matrix4.buildD();
	}

	static Matrix4F perspectiveF(double fov, float aspectRatio, float nearPlane, float farPlane) {
		double f = (float) (1.0D / Math.tan(fov * Math.PI / 180F / 2.0D));
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = f / aspectRatio;
		matrix4.m11 = f;
		matrix4.m22 = (farPlane + nearPlane) / (nearPlane - farPlane);
		matrix4.m33 = 0.0F;
		matrix4.m32 = -1.0F;
		matrix4.m23 = 2.0F * farPlane * nearPlane / (nearPlane - farPlane);
		return matrix4.buildF();
	}

	static Matrix4D perspectiveInfinity(double fov, double aspectRatio, double nearPlane) {
		double f = (1.0D / Math.tan(fov * Math.PI / 180F / 2.0D));
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = f / aspectRatio;
		matrix4.m11 = f;
		matrix4.m22 = -1.0F;
		matrix4.m33 = 0.0F;
		matrix4.m32 = -1.0F;
		matrix4.m23 = -2.0F * nearPlane;
		return matrix4.buildD();
	}

	static Matrix4F perspectiveInfinityF(double fov, float aspectRatio, float nearPlane) {
		float f = (float) (1.0D / Math.tan(fov * Math.PI / 180F / 2.0D));
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = f / aspectRatio;
		matrix4.m11 = f;
		matrix4.m22 = -1.0F;
		matrix4.m33 = 0.0F;
		matrix4.m32 = -1.0F;
		matrix4.m23 = -2.0F * nearPlane;
		return matrix4.buildF();
	}

	static Matrix4D orthographic(double width, double height, double nearPlane, double farPlane) {
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = 2.0F / width;
		matrix4.m11 = 2.0F / height;
		double f = farPlane - nearPlane;
		matrix4.m22 = -2.0F / f;
		matrix4.m33 = 1.0F;
		matrix4.m03 = -1.0F;
		matrix4.m13 = -1.0F;
		matrix4.m23 = -(farPlane + nearPlane) / f;
		return matrix4.buildD();
	}

	static Matrix4F orthographicF(float width, float height, float nearPlane, float farPlane) {
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = 2.0F / width;
		matrix4.m11 = 2.0F / height;
		float f = farPlane - nearPlane;
		matrix4.m22 = -2.0F / f;
		matrix4.m33 = 1.0F;
		matrix4.m03 = -1.0F;
		matrix4.m13 = -1.0F;
		matrix4.m23 = -(farPlane + nearPlane) / f;
		return matrix4.buildF();
	}

	default Matrix4D add(Matrix4 other) {
		return new Matrix4D(
				this.m00() + other.m00(), this.m01() + other.m01(), this.m02() + other.m02(), this.m03() + other.m03(),
				this.m10() + other.m10(), this.m11() + other.m11(), this.m12() + other.m12(), this.m13() + other.m13(),
				this.m20() + other.m20(), this.m21() + other.m21(), this.m22() + other.m22(), this.m23() + other.m23(),
				this.m30() + other.m30(), this.m31() + other.m31(), this.m32() + other.m32(), this.m33() + other.m33()
		);
	}

	default Matrix4F addF(Matrix4 other) {
		return new Matrix4F(
				this.m00f() + other.m00f(), this.m01f() + other.m01f(), this.m02f() + other.m02f(), this.m03f() + other.m03f(),
				this.m10f() + other.m10f(), this.m11f() + other.m11f(), this.m12f() + other.m12f(), this.m13f() + other.m13f(),
				this.m20f() + other.m20f(), this.m21f() + other.m21f(), this.m22f() + other.m22f(), this.m23f() + other.m23f(),
				this.m30f() + other.m30f(), this.m31f() + other.m31f(), this.m32f() + other.m32f(), this.m33f() + other.m33f()
		);
	}

	default Matrix4D multiplyBackward(Matrix4 matrix) {
		return matrix.multiply(this);
	}

	default Matrix4F multiplyBackwardF(Matrix4 matrix) {
		return matrix.multiplyF(this);
	}

	static Matrix4D makeScale(float x, float y, float z) {
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = x;
		matrix4.m11 = y;
		matrix4.m22 = z;
		//matrix4f.m33 = 1.0f;
		return matrix4.buildD();
	}

	static Matrix4F makeScaleF(double x, double y, double z) {
		Matrix4Builder matrix4 = new Matrix4Builder();
		matrix4.m00 = x;
		matrix4.m11 = y;
		matrix4.m22 = z;
		//matrix4f.m33 = 1.0f;
		return matrix4.buildF();
	}

	static Matrix4D makeTranslation(double x, double y, double z) {
		return new Matrix4D(
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1
		);
	}

	static Matrix4F makeTranslationF(float x, float y, float z) {
		return new Matrix4F(
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1
		);
		//Matrix4Builder matrix4 = new Matrix4Builder();
		////this.m00 = 1.0F;
		////this.m11 = 1.0F;
		////this.m22 = 1.0F;
		////this.m33 = 1.0F;
		//matrix4.m03 = x;
		//matrix4.m13 = y;
		//matrix4.m23 = z;
		//return matrix4.buildF();
	}

	@Deprecated
	@NoArgsConstructor
	public static class Matrix4Builder {
		private double m00 = 1;
		private double m01 = 0;
		private double m02 = 0;
		private double m03 = 0;
		private double m10 = 0;
		private double m11 = 1;
		private double m12 = 0;
		private double m13 = 0;
		private double m20 = 0;
		private double m21 = 0;
		private double m22 = 1;
		private double m23 = 0;
		private double m30 = 0;
		private double m31 = 0;
		private double m32 = 0;
		private double m33 = 1;

		public Matrix4Builder(Matrix4 m) {
			this.m00 = m.m00();
			this.m01 = m.m01();
			this.m02 = m.m02();
			this.m03 = m.m03();
			this.m10 = m.m10();
			this.m11 = m.m11();
			this.m12 = m.m12();
			this.m13 = m.m13();
			this.m20 = m.m20();
			this.m21 = m.m21();
			this.m22 = m.m22();
			this.m23 = m.m23();
			this.m30 = m.m30();
			this.m31 = m.m31();
			this.m32 = m.m32();
			this.m33 = m.m33();
		}

		public Matrix4Builder m00(double m00) {
			this.m00 = m00;
			return this;
		}

		public Matrix4Builder m01(double m01) {
			this.m01 = m01;
			return this;
		}

		public Matrix4Builder m02(double m02) {
			this.m02 = m02;
			return this;
		}

		public Matrix4Builder m03(double m03) {
			this.m03 = m03;
			return this;
		}

		public Matrix4Builder m10(double m10) {
			this.m10 = m10;
			return this;
		}

		public Matrix4Builder m11(double m11) {
			this.m11 = m11;
			return this;
		}

		public Matrix4Builder m12(double m12) {
			this.m12 = m12;
			return this;
		}

		public Matrix4Builder m13(double m13) {
			this.m13 = m13;
			return this;
		}

		public Matrix4Builder m20(double m20) {
			this.m20 = m20;
			return this;
		}

		public Matrix4Builder m21(double m21) {
			this.m21 = m21;
			return this;
		}

		public Matrix4Builder m22(double m22) {
			this.m22 = m22;
			return this;
		}

		public Matrix4Builder m23(double m23) {
			this.m23 = m23;
			return this;
		}

		public Matrix4Builder m30(double m30) {
			this.m30 = m30;
			return this;
		}

		public Matrix4Builder m31(double m31) {
			this.m31 = m31;
			return this;
		}

		public Matrix4Builder m32(double m32) {
			this.m32 = m32;
			return this;
		}

		public Matrix4Builder m33(double m33) {
			this.m33 = m33;
			return this;
		}

		public Matrix4D buildD() {
			return new Matrix4D(
					m00, m01, m02, m03,
					m10, m11, m12, m13,
					m20, m21, m22, m23,
					m30, m31, m32, m33
			);
		}

		public Matrix4F buildF() {
			return new Matrix4F(
					(float) m00, (float) m01, (float) m02, (float) m03,
					(float) m10, (float) m11, (float) m12, (float) m13,
					(float) m20, (float) m21, (float) m22, (float) m23,
					(float) m30, (float) m31, (float) m32, (float) m33
			);
		}
	}
}
