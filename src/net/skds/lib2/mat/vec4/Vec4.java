package net.skds.lib2.mat.vec4;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.Vector;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.mat.vec3.Vec3I;

// TODO json
@SuppressWarnings("unused")
public sealed interface Vec4 extends Vector permits Vec4D, Vec4F, Vec4I, Quat {

	@Override
	default int dimension() {
		return 4;
	}

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

	default int xi() {
		return (int) x();
	}

	default int yi() {
		return (int) y();
	}

	default int zi() {
		return (int) z();
	}

	default int wi() {
		return (int) z();
	}

	
	default int floorX() {
		return FastMath.floor(x());
	}

	default int floorY() {
		return FastMath.floor(y());
	}

	default int floorZ() {
		return FastMath.floor(z());
	}

	default int floorW() {
		return FastMath.floor(w());
	}

	default int ceilX() {
		return FastMath.ceil(x());
	}

	default int ceilY() {
		return FastMath.ceil(y());
	}

	default int ceilZ() {
		return FastMath.ceil(z());
	}

	default int ceilW() {
		return FastMath.ceil(w());
	}

	default int roundX() {
		return FastMath.round(x());
	}

	default int roundY() {
		return FastMath.round(y());
	}

	default int roundZ() {
		return FastMath.round(z());
	}

	default int roundW() {
		return FastMath.round(w());
	}

	@Override
	default double get(int i) {
		return switch (i) {
			case 0 -> x();
			case 1 -> y();
			case 2 -> z();
			case 3 -> w();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int getI(int i) {
		return switch (i) {
			case 0 -> xi();
			case 1 -> yi();
			case 2 -> zi();
			case 3 -> wi();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default float getF(int i) {
		return switch (i) {
			case 0 -> xf();
			case 1 -> yf();
			case 2 -> zf();
			case 3 -> wf();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}


	@Override
	default int floor(int i) {
		return switch (i) {
			case 0 -> floorX();
			case 1 -> floorY();
			case 2 -> floorZ();
			case 3 -> floorW();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int ceil(int i) {
		return switch (i) {
			case 0 -> ceilX();
			case 1 -> ceilY();
			case 2 -> ceilZ();
			case 3 -> ceilW();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}


	@Override
	default int round(int i) {
		return switch (i) {
			case 0 -> roundX();
			case 1 -> roundY();
			case 2 -> roundZ();
			case 3 -> roundW();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	static boolean equals(Vec4 v1, Vec4 v2) {
		if (v1 == v2) {
			return true;
		} else if ((v1 == null) != (v2 == null)) {
			return false;
		} else {
			if (v1.x() != v2.x()) {
				return false;
			} else if (v1.y() != v2.y()) {
				return false;
			} else if (v1.z() != v2.z()) {
				return false;
			} else {
				return v1.w() == v2.w();
			}
		}
	}

	static int hashCode(Vec4 vec) {
		int i = Double.hashCode(vec.x());
		i = 31 * i + Double.hashCode(vec.y());
		i = 31 * i + Double.hashCode(vec.z());
		return 31 * i + Double.hashCode(vec.w());
	}

	default double length() {
		return Math.sqrt(this.x() * this.x() + this.y() * this.y() + this.z() * this.z() + this.w() * this.w());
	}

	default double lengthSquared() {
		return this.x() * this.x() + this.y() * this.y() + this.z() * this.z() + this.w() * this.w();
	}

	static double length(double x, double y, double z, double w) {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}

	static double lengthSquared(double x, double y, double z, double w) {
		return x * x + y * y + z * z + w * w;
	}

	static float lengthF(float x, float y, float z, float w) {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	static float lengthSquaredF(float x, float y, float z, float w) {
		return x * x + y * y + z * z + w * w;
	}

	default float lengthF() {
		return (float) Math.sqrt(this.xf() * this.xf() + this.yf() * this.yf() + this.zf() * this.zf() + this.wf() * this.wf());
	}

	default float lengthSquaredF() {
		return this.xf() * this.xf() + this.yf() * this.yf() + this.zf() * this.zf() + this.wf() * this.wf();
	}

	default Vec4D transform(Matrix4 matrixIn) {
		final double x = matrixIn.m00() * this.x() + matrixIn.m01() * this.y() + matrixIn.m02() * this.z() + matrixIn.m03() * this.w();
		final double y = matrixIn.m10() * this.x() + matrixIn.m11() * this.y() + matrixIn.m12() * this.z() + matrixIn.m13() * this.w();
		final double z = matrixIn.m20() * this.x() + matrixIn.m21() * this.y() + matrixIn.m22() * this.z() + matrixIn.m23() * this.w();
		final double w = matrixIn.m30() * this.x() + matrixIn.m31() * this.y() + matrixIn.m32() * this.z() + matrixIn.m33() * this.w();
		return new Vec4D(x, y, z, w);
	}

	default Vec4F transformF(Matrix4 matrixIn) {
		final float x = matrixIn.m00f() * this.xf() + matrixIn.m01f() * this.yf() + matrixIn.m02f() * this.zf() + matrixIn.m03f() * this.wf();
		final float y = matrixIn.m10f() * this.xf() + matrixIn.m11f() * this.yf() + matrixIn.m12f() * this.zf() + matrixIn.m13f() * this.wf();
		final float z = matrixIn.m20f() * this.xf() + matrixIn.m21f() * this.yf() + matrixIn.m22f() * this.zf() + matrixIn.m23f() * this.wf();
		final float w = matrixIn.m30f() * this.xf() + matrixIn.m31f() * this.yf() + matrixIn.m32f() * this.zf() + matrixIn.m33f() * this.wf();
		return new Vec4F(x, y, z, w);
	}

	@Override
	default Vec4I getAsIntVec() {
		return new Vec4I(this.xi(), this.yi(), this.zi(), this.wi());
	}

	@Override
	default Vec4F getAsFloatVec() {
		return new Vec4F(this.xf(), this.yf(), this.zf(), this.wf());
	}

	@Override
	default Vec4D getAsDoubleVec() {
		return new Vec4D(this.x(), this.y(), this.z(), this.w());
	}

	default Vec3I getAsIntVec3() {
		int w = this.wi();
		return new Vec3I(this.xi()/ w, this.yi() / w, this.zi() / w);
	}

	default Vec3F getAsFloatVec3() {
		float w = this.wf();
		return new Vec3F(this.xf() / w, this.yf() / w, this.zf() / w);
	}

	default Vec3D getAsDoubleVec3() {
		double w = this.w();
		return new Vec3D(this.x() / w, this.y() / w, this.z() / w);
	}
}
