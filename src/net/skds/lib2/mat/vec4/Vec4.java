package net.skds.lib2.mat.vec4;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.Vector;
import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.quat.Quat;
import net.skds.lib2.mat.vec3.Vec3D;
import net.skds.lib2.mat.vec3.Vec3F;
import net.skds.lib2.mat.vec3.Vec3I;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;

import static net.skds.lib2.io.sosison.SosisonEntryType.END_OBJECT;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@DefaultCodec(Vec4.Codec.class)
public sealed interface Vec4 extends Vector permits Vec4D, Vec4F, Vec4I {

	Vec4D XN = new Vec4D(-1.0D, 0.0D, 0.0D, 0.0D);
	Vec4D XP = new Vec4D(1.0D, 0.0D, 0.0D, 0.0D);
	Vec4D YN = new Vec4D(0.0D, -1.0D, 0.0D, 0.0D);
	Vec4D YP = new Vec4D(0.0D, 1.0D, 0.0D, 0.0D);
	Vec4D ZN = new Vec4D(0.0D, 0.0D, -1.0D, 0.0D);
	Vec4D ZP = new Vec4D(0.0D, 0.0D, 1.0D, 0.0D);
	Vec4D WN = new Vec4D(0.0D, 0.0D, 0.0D, -1.0D);
	Vec4D WP = new Vec4D(0.0D, 0.0D, 0.0D, 1.0D);
	Vec4D SINGLE = new Vec4D(1.0D, 1.0D, 1.0D, 1.0D);
	Vec4D ZERO = new Vec4D(0.0D, 0.0D, 0.0D, 0.0D);

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
		return (int) w();
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

	static Vec4D of(double x, double y, double z, double w) {
		return new Vec4D(x, y, z, w);
	}

	static Vec4F of(float x, float y, float z, float w) {
		return new Vec4F(x, y, z, w);
	}

	static Vec4I of(int x, int y, int z, int w) {
		return new Vec4I(x, y, z, w);
	}

	static Vec4D of(double size) {
		return new Vec4D(size, size, size, size);
	}

	static Vec4F of(float size) {
		return new Vec4F(size, size, size, size);
	}

	static Vec4I of(int size) {
		return new Vec4I(size, size, size, size);
	}

	static Vec4D randomNormal(Random r) {
		return normalized(r.nextFloat() - .5, r.nextFloat() - .5, r.nextFloat() - .5, r.nextFloat() - .5);
	}

	static Vec4D normalized(double x, double y, double z, double w) {
		double x2 = x * x;
		double y2 = y * y;
		double z2 = z * z;
		double w2 = w * w;
		double k = FastMath.invSqrt(x2 + y2 + z2 + w2 + Double.MIN_NORMAL);
		return new Vec4D(x * k, y * k, z * k, w * k);
	}

	static Vec4F randomNormalF(Random r) {
		return normalizedF(r.nextFloat() - .5f, r.nextFloat() - .5f, r.nextFloat() - .5f, r.nextFloat() - .5f);
	}

	static Vec4F normalizedF(float x, float y, float z, float w) {
		float x2 = x * x;
		float y2 = y * y;
		float z2 = z * z;
		float w2 = w * w;
		float k = FastMath.invSqrt(x2 + y2 + z2 + w2 + Float.MIN_NORMAL);
		return new Vec4F(x * k, y * k, z * k, w * k);
	}

	default Vec4D normalize() {
		double x = x();
		double y = y();
		double z = z();
		double w = w();
		double d0 = x * x + y * y + z * z + w * w;
		d0 = FastMath.invSqrt(d0 + Double.MIN_NORMAL);
		return new Vec4D(x * d0, y * d0, z * d0, w * d0);
	}

	default Vec4F normalizeF() {
		float x = xf();
		float y = yf();
		float z = zf();
		float w = wf();
		float d0 = x * x + y * y + z * z + w * w;
		d0 = FastMath.invSqrt(d0 + Float.MIN_NORMAL);
		return new Vec4F(x * d0, y * d0, z * d0, w * d0);
	}

	default Vec4D normalizeScale(double scale) {
		if (scale == 0) return Vec4D.ZERO;
		double x = x();
		double y = y();
		double z = z();
		double w = w();
		double d0 = x * x + y * y + z * z + w * w;
		d0 = FastMath.invSqrt(d0 + Double.MIN_NORMAL) * scale;
		return new Vec4D(x * d0, y * d0, z * d0, w * d0);
	}

	default Vec4F normalizeScaleF(float scale) {
		if (scale == 0) return Vec4F.ZERO;
		float x = xf();
		float y = yf();
		float z = zf();
		float w = wf();
		float d0 = x * x + y * y + z * z + w * w;
		d0 = FastMath.invSqrt(d0 + Float.MIN_NORMAL) * scale;
		return new Vec4F(x * d0, y * d0, z * d0, w * d0);
	}

	static Vec4D randomizeGaussian(Random r, double fraction) {
		double x = r.nextGaussian() * fraction;
		double y = r.nextGaussian() * fraction;
		double z = r.nextGaussian() * fraction;
		double w = r.nextGaussian() * fraction;
		return new Vec4D(x, y, z, w);
	}

	static Vec4F randomizeGaussianF(Random r, float fraction) {
		float x = (float) (r.nextGaussian() * fraction);
		float y = (float) (r.nextGaussian() * fraction);
		float z = (float) (r.nextGaussian() * fraction);
		float w = (float) (r.nextGaussian() * fraction);
		return new Vec4F(x, y, z, w);
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

	default int lengthI() {
		return (int) Math.sqrt(this.xi() * this.xi() + this.yi() * this.yi() + this.zi() * this.zi() + this.wi() * this.wi());
	}

	default int lengthSquaredI() {
		return this.xi() * this.xi() + this.yi() * this.yi() + this.zi() * this.zi() + this.wi() * this.wi();
	}

	static int lengthI(int x, int y, int z, int w) {
		return (int) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	static int lengthSquaredI(int x, int y, int z, int w) {
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

	default Vec4D inverse() {
		return new Vec4D(-x(), -y(), -z(), -w());
	}

	default Vec4I inverseI() {
		return new Vec4I(-xi(), -yi(), -zi(), -wi());
	}

	default Vec4F inverseF() {
		return new Vec4F(-xf(), -yf(), -zf(), -wf());
	}

	default double distanceTo(Vec4 vec) {
		double dx = vec.x() - x();
		double dy = vec.y() - y();
		double dz = vec.z() - z();
		double dw = vec.w() - w();
		return Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
	}

	default double distanceTo(double x2, double y2, double z2, double w2) {
		x2 -= x();
		y2 -= y();
		z2 -= z();
		w2 -= w();
		return Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2 + w2 * w2);
	}

	default double squareDistanceTo(Vec4 vec) {
		double dx = vec.x() - x();
		double dy = vec.y() - y();
		double dz = vec.z() - z();
		double dw = vec.w() - w();
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	default double squareDistanceTo(double dx, double dy, double dz, double dw) {
		dx -= x();
		dy -= y();
		dz -= z();
		dw -= w();
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	default float distanceToF(Vec4 vec) {
		float dx = vec.xf() - xf();
		float dy = vec.yf() - yf();
		float dz = vec.zf() - zf();
		float dw = vec.wf() - wf();
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
	}

	default float distanceToF(float x2, float y2, float z2, float w2) {
		x2 -= xf();
		y2 -= yf();
		z2 -= zf();
		w2 -= wf();
		return (float) Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2 + w2 * w2);
	}

	default float squareDistanceToF(Vec4 vec) {
		float dx = vec.xf() - xf();
		float dy = vec.yf() - yf();
		float dz = vec.zf() - zf();
		float dw = vec.wf() - wf();
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	default float squareDistanceToF(float x2, float y2, float z2, float w2) {
		x2 -= xf();
		y2 -= yf();
		z2 -= zf();
		w2 -= wf();
		return x2 * x2 + y2 * y2 + z2 * z2 + w2 * w2;
	}

	default double dot(Vec4 vec) {
		return this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z() + this.w() * vec.w();
	}

	default double dot(double x2, double y2, double z2, double w2) {
		return this.x() * x2 + this.y() * y2 + this.z() * z2 + this.w() * w2;
	}

	static double dot(double x1, double y1, double z1, double w1, double x2, double y2, double z2, double w2) {
		return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
	}

	default int dotI(Vec4 vec) {
		return this.xi() * vec.xi() + this.yi() * vec.yi() + this.zi() * vec.zi() + this.wi() * vec.wi();
	}

	default int dotI(int x2, int y2, int z2, int w2) {
		return this.xi() * x2 + this.yi() * y2 + this.zi() * z2 + this.wi() * w2;
	}

	static int dotI(int x1, int y1, int z1, int w1, int x2, int y2, int z2, int w2) {
		return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
	}

	default float dotF(Vec4 vec) {
		return this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf() + this.wf() * vec.wf();
	}

	default float dotF(float x2, float y2, float z2, float w2) {
		return this.xf() * x2 + this.yf() * y2 + this.zf() * z2 + this.wf() * w2;
	}

	static float dotF(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		return x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
	}

	default double projOn(Vec4 vec) {
		double l = vec.length();
		double ret = this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z() + this.w() * vec.w();
		return l == 0 ? ret : ret / l;
	}

	default double projOn(double x2, double y2, double z2, double w2) {
		double l = length(x2, y2, z2, w2);
		double ret = this.x() * x2 + this.y() * y2 + this.z() * z2 + this.w() * w2;
		return l == 0 ? ret : ret / l;
	}

	default float projOnF(Vec4 vec) {
		float l = vec.lengthF();
		float ret = this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf() + this.wf() * vec.wf();
		return l == 0 ? ret : ret / l;
	}

	default float projOnF(float x2, float y2, float z2, float w2) {
		float l = lengthF(x2, y2, z2, w2);
		float ret = this.xf() * x2 + this.yf() * y2 + this.zf() * z2 + this.wf() * w2;
		return l == 0 ? ret : ret / l;
	}

	default Vec4D add(Vec4 vec) {
		return new Vec4D(
				this.x() + vec.x(),
				this.y() + vec.y(),
				this.z() + vec.z(),
				this.w() + vec.w()
		);
	}

	default Vec4D add(double x2, double y2, double z2, double w2) {
		return new Vec4D(
				this.x() + x2,
				this.y() + y2,
				this.z() + z2,
				this.w() + w2
		);
	}

	default Vec4I addI(Vec4 vec) {
		return new Vec4I(
				this.xi() + vec.xi(),
				this.yi() + vec.yi(),
				this.zi() + vec.zi(),
				this.wi() + vec.wi()
		);
	}

	default Vec4I addI(int x2, int y2, int z2, int w2) {
		return new Vec4I(
				this.xi() + x2,
				this.yi() + y2,
				this.zi() + z2,
				this.wi() + w2
		);
	}

	default Vec4F addF(Vec4 vec) {
		return new Vec4F(
				this.xf() + vec.xf(),
				this.yf() + vec.yf(),
				this.zf() + vec.zf(),
				this.wf() + vec.wf()
		);
	}

	default Vec4F addF(float x2, float y2, float z2, float w2) {
		return new Vec4F(
				this.xf() + x2,
				this.yf() + y2,
				this.zf() + z2,
				this.wf() + w2
		);
	}

	default Vec4D scale(Vec4 scale) {
		return new Vec4D(
				this.x() * scale.x(),
				this.y() * scale.y(),
				this.z() * scale.z(),
				this.w() * scale.w()
		);
	}

	default Vec4D scale(double sx, double sy, double sz, double sw) {
		return new Vec4D(
				this.x() * sx,
				this.y() * sy,
				this.z() * sz,
				this.w() * sw
		);
	}

	default Vec4D scale(double scale) {
		return new Vec4D(
				this.x() * scale,
				this.y() * scale,
				this.z() * scale,
				this.w() * scale
		);
	}

	default Vec4I scaleI(Vec4 scale) {
		return new Vec4I(
				this.xi() * scale.xi(),
				this.yi() * scale.yi(),
				this.zi() * scale.zi(),
				this.wi() * scale.wi()
		);
	}

	default Vec4I scaleI(int sx, int sy, int sz, int sw) {
		return new Vec4I(
				this.xi() * sx,
				this.yi() * sy,
				this.zi() * sz,
				this.wi() * sw
		);
	}

	default Vec4I scaleI(int scale) {
		return new Vec4I(
				this.xi() * scale,
				this.yi() * scale,
				this.zi() * scale,
				this.wi() * scale
		);
	}

	default Vec4F scaleF(Vec4 scale) {
		return new Vec4F(
				this.xf() * scale.xf(),
				this.yf() * scale.yf(),
				this.zf() * scale.zf(),
				this.wf() * scale.wf()
		);
	}

	default Vec4F scaleF(float sx, float sy, float sz, float sw) {
		return new Vec4F(
				this.xf() * sx,
				this.yf() * sy,
				this.zf() * sz,
				this.wf() * sw
		);
	}

	default Vec4F scaleF(float scale) {
		return new Vec4F(
				this.xf() * scale,
				this.yf() * scale,
				this.zf() * scale,
				this.wf() * scale
		);
	}

	default Vec4D div(Vec4 div) {
		return new Vec4D(
				this.x() / div.x(),
				this.y() / div.y(),
				this.z() / div.z(),
				this.w() / div.w()
		);
	}

	default Vec4D div(double sx, double sy, double sz, double sw) {
		return new Vec4D(
				this.x() / sx,
				this.y() / sy,
				this.z() / sz,
				this.w() / sw
		);
	}

	default Vec4D div(double div) {
		return new Vec4D(
				this.x() / div,
				this.y() / div,
				this.z() / div,
				this.w() / div
		);
	}

	default Vec4F divF(float div) {
		return new Vec4F(
				this.xf() / div,
				this.yf() / div,
				this.zf() / div,
				this.wf() / div
		);
	}

	static Vec4D add(double x1, double y1, double z1, double w1, double x2, double y2, double z2, double w2) {
		return new Vec4D(x1 + x2, y1 + y2, z1 + z2, w1 + w2);
	}

	static Vec4I addI(int x1, int y1, int z1, int w1, int x2, int y2, int z2, int w2) {
		return new Vec4I(x1 + x2, y1 + y2, z1 + z2, w1 + w2);
	}

	static Vec4F addF(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		return new Vec4F(x1 + x2, y1 + y2, z1 + z2, w1 + w2);
	}

	default Vec4D addScale(Vec4 vec, double scale) {
		return new Vec4D(
				this.x() + vec.x() * scale,
				this.y() + vec.y() * scale,
				this.z() + vec.z() * scale,
				this.w() + vec.w() * scale
		);
	}

	default Vec4D addScale(double x2, double y2, double z2, double w2, double scale) {
		return new Vec4D(
				this.x() + x2 * scale,
				this.y() + y2 * scale,
				this.z() + z2 * scale,
				this.w() + w2 * scale
		);
	}

	default Vec4F addScaleF(Vec4 vec, float scale) {
		return new Vec4F(
				this.xf() + vec.xf() * scale,
				this.yf() + vec.yf() * scale,
				this.zf() + vec.zf() * scale,
				this.wf() + vec.wf() * scale
		);
	}

	default Vec4D sub(Vec4 vec) {
		return new Vec4D(
				this.x() - vec.x(),
				this.y() - vec.y(),
				this.z() - vec.z(),
				this.w() - vec.w()
		);
	}

	default Vec4D sub(double x2, double y2, double z2, double w2) {
		return new Vec4D(
				this.x() - x2,
				this.y() - y2,
				this.z() - z2,
				this.w() - w2
		);
	}

	default Vec4I subI(Vec4 vec) {
		return new Vec4I(
				this.xi() - vec.xi(),
				this.yi() - vec.yi(),
				this.zi() - vec.zi(),
				this.wi() - vec.wi()
		);
	}

	default Vec4I subI(int x2, int y2, int z2, int w2) {
		return new Vec4I(
				this.xi() - x2,
				this.yi() - y2,
				this.zi() - z2,
				this.wi() - w2
		);
	}

	default Vec4F subF(Vec4 vec) {
		return new Vec4F(
				this.xf() - vec.xf(),
				this.yf() - vec.yf(),
				this.zf() - vec.zf(),
				this.wf() - vec.wf()
		);
	}

	default Vec4F subF(float x2, float y2, float z2, float w2) {
		return new Vec4F(
				this.xf() - x2,
				this.yf() - y2,
				this.zf() - z2,
				this.wf() - w2
		);
	}

	static Vec4D sub(double x1, double y1, double z1, double w1, double x2, double y2, double z2, double w2) {
		return new Vec4D(x1 - x2, y1 - y2, z1 - z2, w1 - w2);
	}

	static Vec4I subI(int x1, int y1, int z1, int w1, int x2, int y2, int z2, int w2) {
		return new Vec4I(x1 - x2, y1 - y2, z1 - z2, w1 - w2);
	}

	static Vec4F subF(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2) {
		return new Vec4F(x1 - x2, y1 - y2, z1 - z2, w1 - w2);
	}

	default Vec4D invSub(Vec4 vec) {
		return new Vec4D(
				-this.x() - vec.x(),
				-this.y() - vec.y(),
				-this.z() - vec.z(),
				-this.w() - vec.w()
		);
	}

	default Vec4I invSubI(Vec4 vec) {
		return new Vec4I(
				-this.xi() - vec.xi(),
				-this.yi() - vec.yi(),
				-this.zi() - vec.zi(),
				-this.wi() - vec.wi()
		);
	}

	default Vec4F invSubF(Vec4 vec) {
		return new Vec4F(
				-this.xf() - vec.xf(),
				-this.yf() - vec.yf(),
				-this.zf() - vec.zf(),
				-this.wf() - vec.wf()
		);
	}

	default Vec4D lerp(Vec4 to, double part) {
		final double x = this.x() + (to.x() - this.x()) * part;
		final double y = this.y() + (to.y() - this.y()) * part;
		final double z = this.z() + (to.z() - this.z()) * part;
		final double w = this.w() + (to.w() - this.w()) * part;
		return new Vec4D(x, y, z, w);
	}

	default Vec4F lerpF(Vec4 to, float part) {
		final float x = this.xf() + (to.xf() - this.xf()) * part;
		final float y = this.yf() + (to.yf() - this.yf()) * part;
		final float z = this.zf() + (to.zf() - this.zf()) * part;
		final float w = this.wf() + (to.wf() - this.wf()) * part;
		return new Vec4F(x, y, z, w);
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

	// TODO не версия Vec4
	default Vec4D transform(Quat quaternionIn) {
		final double x = quaternionIn.x();
		final double y = quaternionIn.y();
		final double z = quaternionIn.z();
		final double w = quaternionIn.w();
		final double fx22 = 2.0 * x * x;
		final double fy22 = 2.0 * y * y;
		final double fz22 = 2.0 * z * z;
		final double m00 = 1.0 - fy22 - fz22;
		final double m11 = 1.0 - fz22 - fx22;
		final double m22 = 1.0 - fx22 - fy22;
		final double xy = x * y;
		final double yz = y * z;
		final double zx = z * x;
		final double xw = x * w;
		final double yw = y * w;
		final double zw = z * w;
		final double m10 = 2.0 * (xy + zw);
		final double m01 = 2.0 * (xy - zw);
		final double m20 = 2.0 * (zx - yw);
		final double m02 = 2.0 * (zx + yw);
		final double m21 = 2.0 * (yz + xw);
		final double m12 = 2.0 * (yz - xw);
		final double nx = m00 * this.x() + m01 * this.y() + m02 * this.z();
		final double ny = m10 * this.x() + m11 * this.y() + m12 * this.z();
		final double nz = m20 * this.x() + m21 * this.y() + m22 * this.z();
		return new Vec4D(nx, ny, nz, this.w());
	}

	// TODO не версия Vec4
	default Vec4F transformF(Quat quaternionIn) {
		final float x = quaternionIn.xf();
		final float y = quaternionIn.yf();
		final float z = quaternionIn.zf();
		final float w = quaternionIn.wf();
		final float fx22 = 2.0f * x * x;
		final float fy22 = 2.0f * y * y;
		final float fz22 = 2.0f * z * z;
		final float m00 = 1.0f - fy22 - fz22;
		final float m11 = 1.0f - fz22 - fx22;
		final float m22 = 1.0f - fx22 - fy22;
		final float xy = x * y;
		final float yz = y * z;
		final float zx = z * x;
		final float xw = x * w;
		final float yw = y * w;
		final float zw = z * w;
		final float m10 = 2.0f * (xy + zw);
		final float m01 = 2.0f * (xy - zw);
		final float m20 = 2.0f * (zx - yw);
		final float m02 = 2.0f * (zx + yw);
		final float m21 = 2.0f * (yz + xw);
		final float m12 = 2.0f * (yz - xw);
		final float nx = m00 * this.xf() + m01 * this.yf() + m02 * this.zf();
		final float ny = m10 * this.xf() + m11 * this.yf() + m12 * this.zf();
		final float nz = m20 * this.xf() + m21 * this.yf() + m22 * this.zf();
		return new Vec4F(nx, ny, nz, this.wf());
	}

	default Vec4D clamp(double min, double max) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		double w = this.w();
		boolean change = false;
		if (x > max) {
			x = max;
			change = true;
		} else if (x < min) {
			x = min;
			change = true;
		}
		if (y > max) {
			y = max;
			change = true;
		} else if (y < min) {
			y = min;
			change = true;
		}
		if (z > max) {
			z = max;
			change = true;
		} else if (z < min) {
			z = min;
			change = true;
		}
		if (w > max) {
			w = max;
			change = true;
		} else if (w < min) {
			w = min;
			change = true;
		}
		return change ? new Vec4D(x, y, z, w) : this.getAsDoubleVec();
	}

	default Vec4I clampI(int min, int max) {
		int x = this.xi();
		int y = this.yi();
		int z = this.zi();
		int w = this.wi();
		boolean change = false;
		if (x > max) {
			x = max;
			change = true;
		} else if (x < min) {
			x = min;
			change = true;
		}
		if (y > max) {
			y = max;
			change = true;
		} else if (y < min) {
			y = min;
			change = true;
		}
		if (z > max) {
			z = max;
			change = true;
		} else if (z < min) {
			z = min;
			change = true;
		}
		if (w > max) {
			w = max;
			change = true;
		} else if (w < min) {
			w = min;
			change = true;
		}
		return change ? new Vec4I(x, y, z, w) : this.getAsIntVec();
	}

	default Vec4F clampF(float min, float max) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		float w = this.wf();
		boolean change = false;
		if (x > max) {
			x = max;
			change = true;
		} else if (x < min) {
			x = min;
			change = true;
		}
		if (y > max) {
			y = max;
			change = true;
		} else if (y < min) {
			y = min;
			change = true;
		}
		if (z > max) {
			z = max;
			change = true;
		} else if (z < min) {
			z = min;
			change = true;
		}
		if (w > max) {
			w = max;
			change = true;
		} else if (w < min) {
			w = min;
			change = true;
		}
		return change ? new Vec4F(x, y, z, w) : this.getAsFloatVec();
	}

	default Vec4D flip() {
		return new Vec4D(
				1d / this.x(),
				1d / this.y(),
				1d / this.z(),
				1d / this.w()
		);
	}

	default Vec4F flipF() {
		return new Vec4F(
				1f / this.xf(),
				1f / this.yf(),
				1f / this.zf(),
				1f / this.wf()
		);
	}

	default Vec4D abs() {
		double x = Math.abs(this.x());
		double y = Math.abs(this.y());
		double z = Math.abs(this.z());
		double w = Math.abs(this.w());
		return new Vec4D(x, y, z, w);
	}

	default Vec4I absI() {
		int x = Math.abs(this.xi());
		int y = Math.abs(this.yi());
		int z = Math.abs(this.zi());
		int w = Math.abs(this.wi());
		return new Vec4I(x, y, z, w);
	}

	default Vec4F absF() {
		float x = Math.abs(this.xf());
		float y = Math.abs(this.yf());
		float z = Math.abs(this.zf());
		float w = Math.abs(this.wf());
		return new Vec4F(x, y, z, w);
	}

	default Vec4D greater(double d) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		double w = this.w();
		if (x < d) x = d;
		if (y < d) y = d;
		if (z < d) z = d;
		if (w < d) w = d;
		return new Vec4D(x, y, z, w);
	}

	default Vec4I greaterI(int i) {
		int x = this.xi();
		int y = this.yi();
		int z = this.zi();
		int w = this.wi();
		if (x < i) x = i;
		if (y < i) y = i;
		if (z < i) z = i;
		if (w < i) w = i;
		return new Vec4I(x, y, z, w);
	}

	default Vec4F greaterF(float f) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		float w = this.wf();
		if (x < f) x = f;
		if (y < f) y = f;
		if (z < f) z = f;
		if (w < f) w = f;
		return new Vec4F(x, y, z, w);
	}

	default Vec4D less(double d) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		double w = this.w();
		if (x > d) x = d;
		if (y > d) y = d;
		if (z > d) z = d;
		if (w > d) w = d;
		return new Vec4D(x, y, z, w);
	}

	default Vec4I lessI(int i) {
		int x = this.xi();
		int y = this.yi();
		int z = this.zi();
		int w = this.wi();
		if (x > i) x = i;
		if (y > i) y = i;
		if (z > i) z = i;
		if (w > i) w = i;
		return new Vec4I(x, y, z, w);
	}

	default Vec4F lessF(float f) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		float w = this.wf();
		if (x > f) x = f;
		if (y > f) y = f;
		if (z > f) z = f;
		if (w > f) w = f;
		return new Vec4F(x, y, z, w);
	}

	default Vec4D aprZeroComp(double lim) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		double w = this.w();
		if (Math.abs(x) < lim) x = 0;
		if (Math.abs(y) < lim) y = 0;
		if (Math.abs(z) < lim) z = 0;
		if (Math.abs(w) < lim) w = 0;

		if (x == 0 && y == 0 && z == 0 && w == 0) return Vec4D.ZERO;
		return new Vec4D(x, y, z, w);
	}

	default Vec4F aprZeroCompF(float lim) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		float w = this.wf();
		if (Math.abs(x) < lim) x = 0;
		if (Math.abs(y) < lim) y = 0;
		if (Math.abs(z) < lim) z = 0;
		if (Math.abs(w) < lim) w = 0;

		if (x == 0 && y == 0 && z == 0 && w == 0) return Vec4F.ZERO;
		return new Vec4F(x, y, z, w);
	}

	default Vec4 aprZeroAll(double lim) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		double w = this.z();
		if (Math.abs(x) < lim && Math.abs(y) < lim && Math.abs(z) < lim && Math.abs(w) < lim) {
			return Vec4D.ZERO;
		}
		return this;
	}

	static Vec4D avg(Vec4... vectors) {
		int c = 0;

		double x = 0, y = 0, z = 0, w = 0;
		for (Vec4 vec : vectors) {
			if (vec != null) {
				c++;
				x += vec.x();
				y += vec.y();
				z += vec.z();
				w += vec.w();
			}
		}

		if (c == 0) return Vec4D.ZERO;
		return new Vec4D(x / c, y / c, z / c, w / c);
	}

	static boolean equals(Vec4 v1, Vec4 v2) {
		if (v1 == v2) return true;
		if (v1 == null || v2 == null) return false;
		return v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z() && v1.w() == v2.w();
	}

	static boolean valueEquals(Vec4 v1, Vec4 v2) {
		return v1.x() == v2.x() && v1.y() == v2.y() && v1.z() == v2.z() && v1.w() == v2.w();
	}

	static int hashCode(Vec4 vec) {
		int i = Double.hashCode(vec.x());
		i = 31 * i + Double.hashCode(vec.y());
		i = 31 * i + Double.hashCode(vec.z());
		return 31 * i + Double.hashCode(vec.w());
	}

	default double[] asArray() {
		return new double[]{x(), y(), z(), w()};
	}

	default int[] asArrayI() {
		return new int[]{xi(), yi(), zi(), wi()};
	}

	default float[] asArrayF() {
		return new float[]{xf(), yf(), zf(), wf()};
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

	default String getAsArrayString() {
		return "[" + this.x() + "," + this.y() + "," + this.z() + "," + this.w() + "]";
	}

	default Vec3F toVec3F() {
		return new Vec3F(xf(), yf(), zf());
	}

	default Vec3D toVec3() {
		return new Vec3D(x(), y(), z());
	}

	default Vec3I toVec3I() {
		return new Vec3I(xi(), yi(), zi());
	}

	final class Codec extends AbstractCodec<Vec4> {

		private final UniversalSerializer<Vec4I> veci = this.registry.getSerializerIndirect(Vec4I.class);
		private final UniversalCodec<Vec4> vecd = this.registry.getCodecIndirect(Vec4D.class);

		public Codec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public String valueAsKeyString(Vec4 val) {
			if (val == null) {
				return null;
			}
			return val.getAsArrayString();
		}

		@Override
		public Vec4 stringKeyToValue(String key) throws IOException {
			return this.vecd.stringKeyToValue(key);
		}

		@Override
		public void write(Vec4 value, UniversalWriter writer) throws IOException {
			if (value instanceof Vec4I vec) {
				this.veci.write(vec, writer);
			} else {
				this.vecd.write(value, writer);
			}
		}

		@Override
		public Vec4 read(UniversalReader reader) throws IOException {
			double x = 0;
			double y = 0;
			double z = 0;
			double w = 0;

			return switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					yield null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readDouble();
					y = reader.readDouble();
					z = reader.readDouble();
					w = reader.readDouble();
					reader.endList();
					yield new Vec4D(x, y, z, w);
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
					yield new Vec4D(x, y, z, w);
				}
				case INT -> new Vec4I(reader.readInt());
				case FLOAT -> new Vec4F(reader.readFloat());
				case LONG -> new Vec4D(reader.readLong());
				case DOUBLE -> new Vec4D(reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}

	static int compare(Vec4 a, Vec4 b) {
		int diffX = Double.compare(a.x(), b.x());
		if (diffX != 0) return diffX;
		int diffY = Double.compare(a.y(), b.y());
		if (diffY != 0) return diffY;
		int diffZ = Double.compare(a.z(), b.z());
		if (diffZ != 0) return diffZ;
		return Double.compare(a.w(), b.w());
	}

	class SkipZeroPredicate implements Predicate<Vec4> {
		@Override
		public boolean test(Vec4 vec4) {
			return Objects.equals(vec4, ZERO);
		}
	}

	class SkipSinglePredicate implements Predicate<Vec4> {
		@Override
		public boolean test(Vec4 vec4) {
			return Objects.equals(vec4, SINGLE);
		}
	}

}