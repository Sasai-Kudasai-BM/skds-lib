package net.skds.lib2.mat.quat;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;

import static net.skds.lib2.io.sosison.SosisonEntryType.END_OBJECT;

@DefaultCodec(Quat.JCodec.class)
@SuppressWarnings("unused")
public sealed interface Quat permits QuatD, QuatF {

	QuatD ONE = new QuatD(0, 0, 0, 1);
	QuatD ROT_180_X = new QuatD(1, 0, 0, 0);
	QuatD ROT_180_Y = new QuatD(0, 1, 0, 0);
	QuatD ROT_180_Z = new QuatD(0, 0, 1, 0);

	double x();

	double y();

	double z();

	double w();

	default float xf() {
		return (float) x();
	}

	default float yf() {
		return (float) y();
	}

	default float zf() {
		return (float) z();
	}

	default float wf() {
		return (float) w();
	}

	static boolean equals(Quat q1, Quat q2) {
		if (q1 == q2) return true;
		if (q1 == null || q2 == null) return false;
		return q1.x() == q2.x() && q1.y() == q2.y() && q1.z() == q2.z() && q1.w() == q2.w();
	}

	static int hashCode(Quat q) {
		int i = Double.hashCode(q.x());
		i = 31 * i + Double.hashCode(q.y());
		i = 31 * i + Double.hashCode(q.z());
		return 31 * i + Double.hashCode(q.w());
	}

	QuatD multiply(Quat q);

	QuatF multiplyF(Quat q);

	QuatD rotateAxisDegrees(Vec3 axis, double angle);

	QuatF rotateAxisDegreesF(Vec3 axis, float angle);

	QuatD spin(Vec3 spin);

	QuatF spinF(Vec3 spin);

	QuatD conjugate();

	QuatF conjugateF();

	QuatD normalize();

	QuatF normalizeF();

	Vec3D forward();

	Vec3F forwardF();

	static QuatD fromAxisDegrees(Vec3 axis, double angle) {
		if (Math.abs(angle) < 1E-30) return Quat.ONE;
		double f = FastMath.sinDegr(angle / 2.0);
		return new QuatD(axis.x() * f, axis.y() * f, axis.z() * f, FastMath.cosDegr(angle / 2.0));
	}

	static QuatD fromAxisRad(Vec3 axis, double angle) {
		if (Math.abs(angle) < 1E-30) return Quat.ONE;
		double f = FastMath.sinRad(angle / 2.0);
		return new QuatD(axis.x() * f, axis.y() * f, axis.z() * f, FastMath.cosRad(angle / 2.0));
	}

	static QuatD fromMatrix(Matrix3 m) {
		double x, y, z, w;
		double tr = m.m00() + m.m11() + m.m22();
		if (tr > 0) {
			double s = Math.sqrt(tr + 1.0) * 2;
			w = 0.25 * s;
			x = (m.m21() - m.m12()) / s;
			y = (m.m02() - m.m20()) / s;
			z = (m.m10() - m.m01()) / s;
		} else if ((m.m00() > m.m11()) && (m.m00() > m.m22())) {
			double s = Math.sqrt(1.0 + m.m00() - m.m11() - m.m22()) * 2;
			w = (m.m21() - m.m12()) / s;
			x = 0.25 * s;
			y = (m.m01() + m.m10()) / s;
			z = (m.m02() + m.m20()) / s;
		} else if (m.m11() > m.m22()) {
			double s = Math.sqrt(1.0 + m.m11() - m.m00() - m.m22()) * 2;
			w = (m.m02() - m.m20()) / s;
			x = (m.m01() + m.m10()) / s;
			y = 0.25 * s;
			z = (m.m12() + m.m21()) / s;
		} else {
			double s = Math.sqrt(1.0 + m.m22() - m.m00() - m.m11()) * 2;
			w = (m.m10() - m.m01()) / s;
			x = (m.m02() + m.m20()) / s;
			y = (m.m12() + m.m21()) / s;
			z = 0.25 * s;
		}
		return new QuatD(x, y, z, w);
	}

	static QuatD fromForward(Vec3 forward) {
		return fromMatrix(Matrix3.fromForward(forward));
	}

	static QuatF fromAxisDegreesF(Vec3 axis, float angle) {
		if (Math.abs(angle) < 1E-30) return QuatF.ONE;
		float f = FastMath.sinDegr(angle / 2.0f);
		return new QuatF(axis.xf() * f, axis.yf() * f, axis.zf() * f, FastMath.cosDegr(angle / 2.0f));
	}

	static QuatF fromAxisRadF(Vec3 axis, float angle) {
		if (Math.abs(angle) < 1E-30) return QuatF.ONE;
		float f = FastMath.sinRad(angle / 2.0f);
		return new QuatF(axis.xf() * f, axis.yf() * f, axis.zf() * f, FastMath.cosRad(angle / 2.0f));
	}

	static QuatF fromMatrixF(Matrix3 m) {
		float x, y, z, w;
		float tr = m.m00f() + m.m11f() + m.m22f();
		if (tr > 0) {
			float s = (float) (Math.sqrt(tr + 1.0f)) * 2;
			w = 0.25f * s;
			x = (m.m21f() - m.m12f()) / s;
			y = (m.m02f() - m.m20f()) / s;
			z = (m.m10f() - m.m01f()) / s;
		} else if ((m.m00() > m.m11()) && (m.m00() > m.m22())) {
			float s = (float) (Math.sqrt(1.0f + m.m00f() - m.m11f() - m.m22f())) * 2;
			w = (m.m21f() - m.m12f()) / s;
			x = 0.25f * s;
			y = (m.m01f() + m.m10f()) / s;
			z = (m.m02f() + m.m20f()) / s;
		} else if (m.m11() > m.m22()) {
			float s = (float) (Math.sqrt(1.0f + m.m11f() - m.m00f() - m.m22f())) * 2;
			w = (m.m02f() - m.m20f()) / s;
			x = (m.m01f() + m.m10f()) / s;
			y = 0.25f * s;
			z = (m.m12f() + m.m21f()) / s;
		} else {
			float s = (float) (Math.sqrt(1.0f + m.m22() - m.m00() - m.m11())) * 2;
			w = (m.m10f() - m.m01f()) / s;
			x = (m.m02f() + m.m20f()) / s;
			y = (m.m12f() + m.m21f()) / s;
			z = 0.25f * s;
		}
		return new QuatF(x, y, z, w);
	}

	Vec3D up();

	Vec3F upF();

	Vec3D left();

	Vec3F leftF();

	static Quat sLerp(Quat qa, Quat qb, double t) {
		double cosHalfTheta = qa.w() * qb.w() + qa.x() * qb.x() + qa.y() * qb.y() + qa.z() * qb.z();
		if (Math.abs(cosHalfTheta) >= 1.0) return qa;
		double qmw, qmx, qmy, qmz;
		double halfTheta = Math.acos(cosHalfTheta);
		double sinHalfTheta = Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);
		if (Math.abs(sinHalfTheta) < 1E-15) {
			return new QuatD(
					(qa.w() + qb.w()) * 0.5,
					(qa.x() + qb.x()) * 0.5,
					(qa.y() + qb.y()) * 0.5,
					(qa.z() + qb.z()) * 0.5
			);
		}
		double ratioA = FastMath.sinRad((1 - t) * halfTheta) / sinHalfTheta;
		double ratioB = FastMath.sinRad(t * halfTheta) / sinHalfTheta;
		return new QuatD(
				qa.w() * ratioA + qb.w() * ratioB,
				qa.x() * ratioA + qb.x() * ratioB,
				qa.y() * ratioA + qb.y() * ratioB,
				qa.z() * ratioA + qb.z() * ratioB
		);
	}

	static Quat sLerpF(Quat qa, Quat qb, float t) {
		float cosHalfTheta = qa.wf() * qb.wf() + qa.xf() * qb.xf() + qa.yf() * qb.yf() + qa.zf() * qb.zf();
		if (Math.abs(cosHalfTheta) >= 1.0) return qa;
		float qmw, qmx, qmy, qmz;
		float halfTheta = (float) Math.acos(cosHalfTheta);
		float sinHalfTheta = (float) Math.sqrt(1.0 - cosHalfTheta * cosHalfTheta);
		if (Math.abs(sinHalfTheta) < 1E-15) {
			return new QuatF(
					(qa.wf() + qb.wf()) * 0.5f,
					(qa.xf() + qb.xf()) * 0.5f,
					(qa.yf() + qb.yf()) * 0.5f,
					(qa.zf() + qb.zf()) * 0.5f
			);
		}
		float ratioA = FastMath.sinRad((1 - t) * halfTheta) / sinHalfTheta;
		float ratioB = FastMath.sinRad(t * halfTheta) / sinHalfTheta;
		return new QuatF(
				qa.wf() * ratioA + qb.wf() * ratioB,
				qa.xf() * ratioA + qb.xf() * ratioB,
				qa.yf() * ratioA + qb.yf() * ratioB,
				qa.zf() * ratioA + qb.zf() * ratioB
		);
	}

	final class JCodec extends AbstractCodec<Quat> {
		public JCodec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Quat value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			writer.writeDouble(value.x());
			writer.writeDouble(value.y());
			writer.writeDouble(value.z());
			writer.writeDouble(value.w());
			writer.endList();
		}

		@Override
		public Quat read(UniversalReader reader) throws IOException {
			double x = 0, y = 0, z = 0, w = 0;
			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readDouble();
					y = reader.readDouble();
					z = reader.readDouble();
					w = reader.readDouble();
					reader.endList();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x" -> x = reader.readDouble();
							case "y" -> y = reader.readDouble();
							case "z" -> z = reader.readDouble();
							case "w" -> w = reader.readDouble();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
				}
				default ->
						throw new ParseException("Unsupported token in quaternion \"" + reader.nextEntryType() + "\"");
			}
			return new QuatD(x, y, z, w);
		}
	}

	class SkipOnePredicate implements Predicate<Quat> {
		@Override
		public boolean test(Quat quat) {
			return Objects.equals(quat, ONE);
		}
	}
}
