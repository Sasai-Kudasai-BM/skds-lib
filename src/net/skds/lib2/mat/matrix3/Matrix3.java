package net.skds.lib2.mat.matrix3;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.utils.linkiges.Obj2DoublePairRecord;
import net.skds.lib2.utils.linkiges.Obj2FloatPairRecord;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.skds.lib2.io.sosison.SosisonEntryType.NULL;

@SuppressWarnings("unused")
@DefaultCodec(Matrix3.JCodec.class)
public sealed interface Matrix3 permits Matrix3D, Matrix3F {

	Matrix3 SINGLE = Matrix3D.SINGLE;

	double m00();

	double m01();

	double m02();

	double m10();

	double m11();

	double m12();

	double m20();

	double m21();

	double m22();

	float m00f();

	float m01f();

	float m02f();

	float m10f();

	float m11f();

	float m12f();

	float m20f();

	float m21f();

	float m22f();

	static boolean equals(Matrix3 m1, Matrix3 m2) {
		if (m1 == m2) {
			return true;
		} else if ((m1 == null) != (m2 == null)) {
			return false;
		} else {
			return m1.m00() == m2.m00() && m1.m01() == m2.m01() && m1.m02() == m2.m02()
					&& m1.m10() == m2.m10() && m1.m11() == m2.m11() && m1.m12() == m2.m12()
					&& m1.m20() == m2.m20() && m1.m21() == m2.m21() && m1.m22() == m2.m22();
		}
	}

	static boolean valueEquals(Matrix3 m1, Matrix3 m2) {
		return m1.m00() == m2.m00() && m1.m01() == m2.m01() && m1.m02() == m2.m02()
				&& m1.m10() == m2.m10() && m1.m11() == m2.m11() && m1.m12() == m2.m12()
				&& m1.m20() == m2.m20() && m1.m21() == m2.m21() && m1.m22() == m2.m22();

	}

	static Matrix3D fromNormals(Vec3[] normals) {
		return new Matrix3D(
				normals[0].x(),
				normals[1].x(),
				normals[2].x(),
				normals[0].y(),
				normals[1].y(),
				normals[2].y(),
				normals[0].z(),
				normals[1].z(),
				normals[2].z()
		);
	}

	static Matrix3F fromNormalsF(Vec3[] normals) {
		return new Matrix3F(
				normals[0].xf(),
				normals[1].xf(),
				normals[2].xf(),
				normals[0].yf(),
				normals[1].yf(),
				normals[2].yf(),
				normals[0].zf(),
				normals[1].zf(),
				normals[2].zf()
		);
	}

	static Matrix3D fromNormals(Vec3 l, Vec3 u, Vec3 f) {
		return new Matrix3D(
				l.x(),
				u.x(),
				f.x(),
				l.y(),
				u.y(),
				f.y(),
				l.z(),
				u.z(),
				f.z()
		);
	}

	static Matrix3F fromNormalsF(Vec3 l, Vec3 u, Vec3 f) {
		return new Matrix3F(
				l.xf(),
				u.xf(),
				f.xf(),
				l.yf(),
				u.yf(),
				f.yf(),
				l.zf(),
				u.zf(),
				f.zf()
		);
	}

	static Matrix3D fromQuat(Quat q) {
		double x = q.x();
		double y = q.y();
		double z = q.z();
		double w = q.w();
		double fx22 = 2.0 * x * x;
		double fy22 = 2.0 * y * y;
		double fz22 = 2.0 * z * z;
		double xy = x * y;
		double yz = y * z;
		double zx = z * x;
		double xw = x * w;
		double yw = y * w;
		double zw = z * w;

		return new Matrix3D(
				1.0 - fy22 - fz22,
				2.0 * (xy - zw),
				2.0 * (zx + yw),
				2.0 * (xy + zw),
				1.0 - fz22 - fx22,
				2.0 * (yz - xw),
				2.0 * (zx - yw),
				2.0 * (yz + xw),
				1.0 - fx22 - fy22
		);
	}

	static Matrix3D fromQuatNS(Quat q, double sx, double sy, double sz) {
		double x = q.x();
		double y = q.y();
		double z = q.z();
		double w = q.w();
		double fx22 = 2.0 * x * x;
		double fy22 = 2.0 * y * y;
		double fz22 = 2.0 * z * z;
		double xy = x * y;
		double yz = y * z;
		double zx = z * x;
		double xw = x * w;
		double yw = y * w;
		double zw = z * w;

		return new Matrix3D(
				sx * (1.0 - fy22 - fz22),
				sy * (2.0 * (xy - zw)),
				sz * (2.0 * (zx + yw)),
				sx * (2.0 * (xy + zw)),
				sy * (1.0 - fz22 - fx22),
				sz * (2.0 * (yz - xw)),
				sx * (2.0 * (zx - yw)),
				sy * (2.0 * (yz + xw)),
				sz * (1.0 - fx22 - fy22)
		);
	}

	static Matrix3F fromQuatF(Quat q) {
		float x = q.xf();
		float y = q.yf();
		float z = q.zf();
		float w = q.wf();
		float fx22 = 2.0f * x * x;
		float fy22 = 2.0f * y * y;
		float fz22 = 2.0f * z * z;
		float xy = x * y;
		float yz = y * z;
		float zx = z * x;
		float xw = x * w;
		float yw = y * w;
		float zw = z * w;

		return new Matrix3F(
				1.0f - fy22 - fz22,
				2.0f * (xy - zw),
				2.0f * (zx + yw),
				2.0f * (xy + zw),
				1.0f - fz22 - fx22,
				2.0f * (yz - xw),
				2.0f * (zx - yw),
				2.0f * (yz + xw),
				1.0f - fx22 - fy22
		);
	}

	static Matrix3F fromQuatNSF(Quat q, float sx, float sy, float sz) {
		float x = q.xf();
		float y = q.yf();
		float z = q.zf();
		float w = q.wf();
		float fx22 = 2.0f * x * x;
		float fy22 = 2.0f * y * y;
		float fz22 = 2.0f * z * z;
		float xy = x * y;
		float yz = y * z;
		float zx = z * x;
		float xw = x * w;
		float yw = y * w;
		float zw = z * w;

		return new Matrix3F(
				sx * (1.0f - fy22 - fz22),
				sy * (2.0f * (xy - zw)),
				sz * (2.0f * (zx + yw)),
				sx * (2.0f * (xy + zw)),
				sy * (1.0f - fz22 - fx22),
				sz * (2.0f * (yz - xw)),
				sx * (2.0f * (zx - yw)),
				sy * (2.0f * (yz + xw)),
				sz * (1.0f - fx22 - fy22)
		);
	}

	static Matrix3D fromForward(Vec3 forward) {
		if (forward.x() == 0 && forward.z() == 0) {
			return fromNormals(Vec3.XP, forward.cross(Vec3.XP), forward);
		}
		Vec3 left = Vec3.normalized(forward.z(), 0, -forward.x());
		return fromNormals(left, forward.cross(left), forward);
	}

	static Matrix3F fromForwardF(Vec3 forward) {
		if (forward.x() == 0 && forward.z() == 0) {
			return fromNormalsF(Vec3.XP, forward.crossF(Vec3.XP), forward);
		}
		Vec3 left = Vec3.normalizedF(forward.zf(), 0, -forward.xf());
		return fromNormalsF(left, forward.crossF(left), forward);
	}

	Matrix3D transpose();

	Matrix3F transposeF();

	Matrix3D invert();

	Matrix3F invertF();

	Obj2DoublePairRecord<Matrix3D> adjugateAndDet();

	Obj2FloatPairRecord<Matrix3F> adjugateAndDetF();

	double det();

	float detF();

	Matrix3D multiply(Matrix3 m2);

	Matrix3F multiplyF(Matrix3 m2);

	Matrix3D multiply(Quat q);

	Matrix3F multiplyF(Quat q);

	Matrix3D scale(double scale);

	Matrix3D scaleNS(double sx, double sy, double sz);

	Matrix3F scaleF(float scale);

	Matrix3F scaleNSF(float sx, float sy, float sz);

	Vec3D getYPRAngles();

	Vec3D[] asNormals();

	Vec3F[] asNormalsF();

	Vec3D left();

	Vec3D leftNorm();

	Vec3F leftF();

	Vec3F leftNormF();

	Vec3D up();

	Vec3D upNorm();

	Vec3F upF();

	Vec3F upNormF();

	Vec3D forward();

	Vec3D forwardNorm();

	Vec3F forwardF();

	Vec3F forwardNormF();

	static Matrix3D rotationFromAToB(Vec3 a, Vec3 b) {
		Vec3 cross = a.cross(b);
		double len = cross.length();
		if (len < 1E-20) {
			return Matrix3D.SINGLE;
		}

		double lenXLen = a.length() * b.length();
		double dot = a.dot(b);
		cross.scale(1 / len);

		double c = dot / lenXLen;
		double s = len / lenXLen;

		double oneMinusC = 1.0 - c;
		double xy = cross.x() * cross.y();
		double yz = cross.y() * cross.z();
		double xz = cross.x() * cross.z();
		double xs = cross.x() * s;
		double ys = cross.y() * s;
		double zs = cross.z() * s;

		return new Matrix3D(
				cross.x() * cross.x() * oneMinusC + c,
				xy * oneMinusC + zs,
				xz * oneMinusC - ys,
				xy * oneMinusC - zs,
				cross.y() * cross.y() * oneMinusC + c,
				yz * oneMinusC + xs,
				xz * oneMinusC + ys,
				yz * oneMinusC - xs,
				cross.z() * cross.z() * oneMinusC + c
		);
	}

	static Matrix3F rotationFromAToBF(Vec3 a, Vec3 b) {
		Vec3 cross = a.crossF(b);
		float len = cross.lengthF();
		if (len < 1E-20) {
			return Matrix3F.SINGLE;
		}

		float lenXLen = a.lengthF() * b.lengthF();
		float dot = a.dotF(b);
		cross = cross.scale(1 / len);

		float c = dot / lenXLen;
		float s = len / lenXLen;

		float oneMinusC = 1.0f - c;
		float xy = cross.xf() * cross.yf();
		float yz = cross.yf() * cross.zf();
		float xz = cross.xf() * cross.zf();
		float xs = cross.xf() * s;
		float ys = cross.yf() * s;
		float zs = cross.zf() * s;

		return new Matrix3F(
				cross.xf() * cross.xf() * oneMinusC + c,
				xy * oneMinusC + zs,
				xz * oneMinusC - ys,
				xy * oneMinusC - zs,
				cross.yf() * cross.yf() * oneMinusC + c,
				yz * oneMinusC + xs,
				xz * oneMinusC + ys,
				yz * oneMinusC - xs,
				cross.zf() * cross.zf() * oneMinusC + c
		);
	}

	final class JCodec extends AbstractCodec<Matrix3> {

		public JCodec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Matrix3 value, UniversalWriter writer) throws IOException {
			writer.beginList();
			writer.writeDouble(value.m00());
			writer.writeDouble(value.m01());
			writer.writeDouble(value.m02());
			writer.writeDouble(value.m10());
			writer.writeDouble(value.m11());
			writer.writeDouble(value.m12());
			writer.writeDouble(value.m20());
			writer.writeDouble(value.m21());
			writer.writeDouble(value.m22());
			writer.endList();
		}

		@Override
		public Matrix3 read(UniversalReader reader) throws IOException {
			if (reader.nextEntryType() == NULL) {
				return Matrix3.SINGLE;
			}
			reader.beginList();
			Matrix3D m = new Matrix3D(
					reader.readDouble(), reader.readDouble(), reader.readDouble(),
					reader.readDouble(), reader.readDouble(), reader.readDouble(),
					reader.readDouble(), reader.readDouble(), reader.readDouble()
			);
			reader.endList();
			return m;
		}
	}
}
