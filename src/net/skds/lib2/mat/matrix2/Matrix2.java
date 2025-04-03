package net.skds.lib2.mat.matrix2;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec2.Vec2;
import net.skds.lib2.mat.vec2.Vec2D;
import net.skds.lib2.mat.vec2.Vec2F;
import net.skds.lib2.mat.vec3.Vec3;

import java.io.IOException;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
@DefaultJsonCodec(Matrix2.JCodec.class)
public sealed interface Matrix2 permits Matrix2D, Matrix2F {

	Matrix2 SINGLE = Matrix2F.SINGLE;

	double m00();

	double m01();

	double m10();

	double m11();

	default float m00f() {
		return (float) m00();
	}

	default float m01f() {
		return (float) m01();
	}

	default float m10f() {
		return (float) m10();
	}

	default float m11f() {
		return (float) m11();
	}

	default double det() {
		return m00() * m11() - m10() * m01();
	}

	default float detF() {
		return m00f() * m11f() - m10f() * m01f();
	}

	static boolean equals(Matrix2 m1, Matrix2 m2) {
		if (m1 == m2) {
			return true;
		} else if ((m1 == null) != (m2 == null)) {
			return false;
		} else {
			return m1.m00() == m2.m00() && m1.m10() == m2.m10() && m1.m11() == m2.m11() && m1.m01() == m2.m01();
		}
	}

	static Matrix2F fromRotationDegrF(float angle) {
		float sin = FastMath.sinDegr(angle);
		float cos = FastMath.cosDegr(angle);
		return new Matrix2F(
				cos,
				-sin,
				sin,
				cos
		);
	}

	static Matrix2D fromNormals(Vec3[] normals) {
		return new Matrix2D(
				normals[0].x(),
				normals[1].x(),
				normals[0].y(),
				normals[1].y()
		);
	}

	static Matrix2F fromNormalsF(Vec3[] normals) {
		return new Matrix2F(
				normals[0].xf(),
				normals[1].xf(),
				normals[0].yf(),
				normals[1].yf()
		);
	}

	static Matrix2D fromNormals(Vec2 l, Vec2 u) {
		return new Matrix2D(
				l.x(),
				u.x(),
				l.y(),
				u.y()
		);
	}

	static Matrix2F fromNormalsF(Vec2 l, Vec2 u) {
		return new Matrix2F(
				l.xf(),
				u.xf(),
				l.yf(),
				u.yf()
		);
	}

	default Matrix2D transpose() {
		return new Matrix2D(
				m00(), m10(),
				m01(), m11()
		);
	}

	default Matrix2F transposeF() {
		return new Matrix2F(
				m00f(), m10f(),
				m01f(), m11f()
		);
	}


	default Matrix2F rotateDegrF(float angle) {
		float sin = FastMath.sinDegr(angle);
		float cos = FastMath.cosDegr(angle);
		return new Matrix2F(
				cos * this.m00f() - sin * this.m10f(),
				cos * this.m01f() - sin * this.m11f(),
				sin * this.m00f() + cos * this.m10f(),
				sin * this.m01f() + cos * this.m11f()
		);
	}

	default Matrix2D multiply(Matrix2 m2) {
		return new Matrix2D(
				this.m00() * m2.m00() + this.m01() * m2.m10(),
				this.m00() * m2.m01() + this.m01() * m2.m11(),
				this.m10() * m2.m00() + this.m11() * m2.m10(),
				this.m10() * m2.m01() + this.m11() * m2.m11()
		);
	}

	default Matrix2F multiplyF(Matrix2 m2) {
		return new Matrix2F(
				this.m00f() * m2.m00f() + this.m01f() * m2.m10f(),
				this.m00f() * m2.m01f() + this.m01f() * m2.m11f(),
				this.m10f() * m2.m00f() + this.m11f() * m2.m10f(),
				this.m10f() * m2.m01f() + this.m11f() * m2.m11f()
		);
	}

	default Matrix2D scale(double scale) {
		return new Matrix2D(
				m00() * scale,
				m01() * scale,
				m10() * scale,
				m11() * scale
		);
	}

	default Matrix2F scaleF(float scale) {
		return new Matrix2F(
				m00f() * scale,
				m01f() * scale,
				m10f() * scale,
				m11f() * scale
		);
	}

	default Vec2D[] asNormals() {
		Vec2D[] norms = new Vec2D[2];
		norms[0] = new Vec2D(m00(), m10());
		norms[1] = new Vec2D(m01(), m11());
		return norms;
	}

	default Vec2F[] asNormalsF() {
		Vec2F[] norms = new Vec2F[3];
		norms[0] = new Vec2F(m00f(), m10f());
		norms[1] = new Vec2F(m01f(), m11f());
		return norms;
	}

	default Vec2D right() {
		return new Vec2D(m00(), m10());
	}

	default Vec2D rightNorm() {
		return Vec2.normalized(m00(), m10());
	}

	default Vec2F rightF() {
		return new Vec2F(m00f(), m10f());
	}

	default Vec2F rightNormF() {
		return Vec2.normalizedF(m00f(), m10f());
	}

	default Vec2D up() {
		return new Vec2D(m01(), m11());
	}

	default Vec2D upNorm() {
		return Vec2.normalized(m01(), m11());
	}

	default Vec2F upF() {
		return new Vec2F(m01f(), m11f());
	}

	default Vec2F upNormF() {
		return Vec2.normalizedF(m01f(), m11f());
	}

	final class JCodec extends AbstractJsonCodec<Matrix2> {

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Matrix2 value, JsonWriter writer) throws IOException {
			writer.beginArray();
			writer.writeFloat(value.m00());
			writer.writeFloat(value.m01());
			writer.writeFloat(value.m10());
			writer.writeFloat(value.m11());
			writer.endArray();
		}

		@Override
		public Matrix2 read(JsonReader reader) throws IOException {
			if (reader.nextEntryType() == JsonEntryType.NULL) {
				return Matrix2.SINGLE;
			}
			reader.beginArray();
			Matrix2D m = new Matrix2D(
					reader.readDouble(), reader.readDouble(),
					reader.readDouble(), reader.readDouble()
			);
			reader.endArray();
			return m;
		}
	}
}
