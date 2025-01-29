package net.skds.lib2.mat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Random;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonSerializer;
import net.skds.lib2.io.json.exception.JsonReadException;

// TODO проверить гетеры
@SuppressWarnings("unused")
@DefaultJsonCodec(Vec3.JCodec.class)
public sealed interface Vec3 extends Vector permits Vec3D, Vec3F, Vec3I, Direction {

	Vec3 XN = Vec3D.XN;
	Vec3 XP = Vec3D.XP;
	Vec3 YN = Vec3D.YN;
	Vec3 YP = Vec3D.YP;
	Vec3 ZN = Vec3D.ZN;
	Vec3 ZP = Vec3D.ZP;
	Vec3 SINGLE = Vec3D.SINGLE;
	Vec3 ZERO = Vec3D.ZERO;

	@Override
	default int dimension() {
		return 3;
	}

	double x();

	double y();

	double z();

	default float xf() {
		return (float) x();
	}

	default float yf() {
		return (float) y();
	}

	default float zf() {
		return (float) z();
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

	default int floorX() {
		return FastMath.floor(x());
	}

	default int floorY() {
		return FastMath.floor(y());
	}

	default int floorZ() {
		return FastMath.floor(z());
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

	default int roundX() {
		return FastMath.round(x());
	}

	default int roundY() {
		return FastMath.round(y());
	}

	default int roundZ() {
		return FastMath.round(z());
	}

	@Override
	default double get(int i) {
		return switch (i) {
			case 0 -> x();
			case 1 -> y();
			case 2 -> z();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int getI(int i) {
		return switch (i) {
			case 0 -> xi();
			case 1 -> yi();
			case 2 -> zi();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default float getF(int i) {
		return switch (i) {
			case 0 -> xf();
			case 1 -> yf();
			case 2 -> zf();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}


	@Override
	default int floor(int i) {
		return switch (i) {
			case 0 -> floorX();
			case 1 -> floorY();
			case 2 -> floorZ();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int ceil(int i) {
		return switch (i) {
			case 0 -> ceilX();
			case 1 -> ceilY();
			case 2 -> ceilZ();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int round(int i) {
		return switch (i) {
			case 0 -> roundX();
			case 1 -> roundY();
			case 2 -> roundZ();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	static Vec3D of(double x, double y, double z) {
		return new Vec3D(x, y, z);
	}

	static Vec3F of(float x, float y, float z) {
		return new Vec3F(x, y, z);
	}

	static Vec3I of(int x, int y, int z) {
		return new Vec3I(x, y, z);
	}

	static Vec3D randomNormal(Random r) {
		return normalized(r.nextFloat() - .5, r.nextFloat() - .5, r.nextFloat() - .5);
	}

	static Vec3D normalized(double x, double y, double z) {
		double x2 = x * x;
		double y2 = y * y;
		double z2 = z * z;
		double k = FastMath.invSqrt(x2 + y2 + z2);
		return new Vec3D(x * k, y * k, z * k);
	}

	static Direction normalizedI(int x, int y, int z) {
		return Direction.getFacing(x, y, z);
	}

	static Vec3F randomNormalF(Random r) {
		return normalizedF(r.nextFloat() - .5f, r.nextFloat() - .5f, r.nextFloat() - .5f);
	}

	static Vec3F normalizedF(float x, float y, float z) {
		float x2 = x * x;
		float y2 = y * y;
		float z2 = z * z;
		float k = FastMath.invSqrt(x2 + y2 + z2);
		return new Vec3F(x * k, y * k, z * k);
	}

	default Vec3D normalize() {
		double x = x();
		double y = y();
		double z = z();
		double d0 = x * x + y * y + z * z;
		d0 = FastMath.invSqrt(d0);
		return new Vec3D(x * d0, y * d0, z * d0);
	}

	default Direction normalizeI() {
		return Direction.getFacing(xi(), yi(), zi());
	}

	default Vec3F normalizeF() {
		float x = xf();
		float y = yf();
		float z = zf();
		float d0 = x * x + y * y + z * z;
		d0 = FastMath.invSqrt(d0);
		return new Vec3F(x * d0, y * d0, z * d0);
	}

	default Vec3D normalizeScale(double scale) {
		double x = x();
		double y = y();
		double z = z();
		double d0 = x * x + y * y + z * z;
		d0 = FastMath.invSqrt(d0) * scale;
		return new Vec3D(x * d0, y * d0, z * d0);
	}

	default Vec3I normalizeScaleI(int scale) {
		return Direction.getFacing(xi(), yi(), zi()).scaleI(scale);
	}

	default Vec3F normalizeScaleF(float scale) {
		float x = xf();
		float y = yf();
		float z = zf();
		float d0 = x * x + y * y + z * z;
		d0 = FastMath.invSqrt(d0) * scale;
		return new Vec3F(x * d0, y * d0, z * d0);
	}

	static Vec3D randomizeGaussian(Random r, double fraction) {
		double x = r.nextGaussian() * fraction;
		double y = r.nextGaussian() * fraction;
		double z = r.nextGaussian() * fraction;
		return new Vec3D(x, y, z);
	}

	static Vec3F randomizeGaussianF(Random r, float fraction) {
		float x = (float) (r.nextGaussian() * fraction);
		float y = (float) (r.nextGaussian() * fraction);
		float z = (float) (r.nextGaussian() * fraction);
		return new Vec3F(x, y, z);
	}

	default double length() {
		return Math.sqrt(this.x() * this.x() + this.y() * this.y() + this.z() * this.z());
	}

	default double lengthSquared() {
		return this.x() * this.x() + this.y() * this.y() + this.z() * this.z();
	}

	static double length(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}

	static double lengthSquared(double x, double y, double z) {
		return x * x + y * y + z * z;
	}

	default int lengthI() {
		return (int) Math.sqrt(this.xi() * this.xi() + this.yi() * this.yi() + this.zi() * this.zi());
	}

	default int lengthSquaredI() {
		return this.xi() * this.xi() + this.yi() * this.yi() + this.zi() * this.zi();
	}

	static int lengthI(int x, int y, int z) {
		return (int) Math.sqrt(x * x + y * y + z * z);
	}

	static int lengthSquaredI(int x, int y, int z) {
		return x * x + y * y + z * z;
	}

	static float lengthF(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	static float lengthSquaredF(float x, float y, float z) {
		return x * x + y * y + z * z;
	}

	default float lengthF() {
		return (float) Math.sqrt(this.xf() * this.xf() + this.yf() * this.yf() + this.zf() * this.zf());
	}

	default float lengthSquaredF() {
		return this.xf() * this.xf() + this.yf() * this.yf() + this.zf() * this.zf();
	}

	default Vec3D inverse() {
		return new Vec3D(-x(), -y(), -z());
	}

	default Vec3I inverseI() {
		return new Vec3I(-xi(), -yi(), -zi());
	}

	default Vec3F inverseF() {
		return new Vec3F(-xf(), -yf(), -zf());
	}

	default double distanceTo(Vec3 vec) {
		double dx = vec.x() - x();
		double dy = vec.y() - y();
		double dz = vec.z() - z();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	default double distanceTo(double x2, double y2, double z2) {
		x2 -= x();
		y2 -= y();
		z2 -= z();
		return Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
	}

	default double squareDistanceTo(Vec3 vec) {
		double dx = vec.x() - x();
		double dy = vec.y() - y();
		double dz = vec.z() - z();
		return dx * dx + dy * dy + dz * dz;
	}

	default double squareDistanceTo(double dx, double dy, double dz) {
		dx -= x();
		dy -= y();
		dz -= z();
		return dx * dx + dy * dy + dz * dz;
	}

	default int distanceToI(Vec3 vec) {
		int dx = vec.xi() - xi();
		int dy = vec.yi() - yi();
		int dz = vec.zi() - zi();
		return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	default int distanceToI(int x2, int y2, int z2) {
		x2 -= xi();
		y2 -= yi();
		z2 -= zi();
		return (int) Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
	}

	default int squareDistanceToI(Vec3 vec) {
		int dx = vec.xi() - xi();
		int dy = vec.yi() - yi();
		int dz = vec.zi() - zi();
		return dx * dx + dy * dy + dz * dz;
	}

	default int squareDistanceToI(int dx, int dy, int dz) {
		dx -= xi();
		dy -= yi();
		dz -= zi();
		return dx * dx + dy * dy + dz * dz;
	}

	default float distanceToF(Vec3 vec) {
		float dx = vec.xf() - xf();
		float dy = vec.yf() - yf();
		float dz = vec.zf() - zf();
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	default float distanceToF(float x2, float y2, float z2) {
		x2 -= xf();
		y2 -= yf();
		z2 -= zf();
		return (float) Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
	}

	default float squareDistanceToF(Vec3 vec) {
		float dx = vec.xf() - xf();
		float dy = vec.yf() - yf();
		float dz = vec.zf() - zf();
		return dx * dx + dy * dy + dz * dz;
	}

	default float squareDistanceToF(float x2, float y2, float z2) {
		x2 -= xf();
		y2 -= yf();
		z2 -= zf();
		return x2 * x2 + y2 * y2 + z2 * z2;
	}

	default double dot(Vec3 vec) {
		return this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z();
	}

	default double dot(double x2, double y2, double z2) {
		return this.x() * x2 + this.y() * y2 + this.z() * z2;
	}

	static double dot(double x1, double y1, double z1, double x2, double y2, double z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	default int dotI(Vec3 vec) {
		return this.xi() * vec.xi() + this.yi() * vec.yi() + this.zi() * vec.zi();
	}

	default int dotI(int x2, int y2, int z2) {
		return this.xi() * x2 + this.yi() * y2 + this.zi() * z2;
	}

	static int dotI(int x1, int y1, int z1, int x2, int y2, int z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	default float dotF(Vec3 vec) {
		return this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf();
	}

	default float dotF(float x2, float y2, float z2) {
		return this.xf() * x2 + this.yf() * y2 + this.zf() * z2;
	}

	static float dotF(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	default Vec3D rotatePitch(double pitch) {
		double f = FastMath.cosDegr(pitch);
		double f1 = FastMath.sinDegr(pitch);
		double d1 = this.y() * f + this.z() * f1;
		double d2 = this.z() * f - this.y() * f1;
		return new Vec3D(this.x(), d1, d2);
	}

	default Vec3D rotateYaw(double yaw) {
		double f = FastMath.cosDegr(yaw);
		double f1 = FastMath.sinDegr(yaw);
		double d0 = this.x() * f + this.z() * f1;
		double d2 = this.z() * f - this.x() * f1;
		return new Vec3D(d0, this.y(), d2);
	}

	default Vec3D rotateRoll(double roll) {
		double f = FastMath.cosDegr(roll);
		double f1 = FastMath.sinDegr(roll);
		double d0 = this.x() * f + this.y() * f1;
		double d1 = this.y() * f - this.x() * f1;
		return new Vec3D(d0, d1, this.z());
	}

	default Vec3F rotatePitchF(float pitch) {
		float f = FastMath.cosDegr(pitch);
		float f1 = FastMath.sinDegr(pitch);
		float d1 = this.yf() * f + this.zf() * f1;
		float d2 = this.zf() * f - this.yf() * f1;
		return new Vec3F(this.xf(), d1, d2);
	}

	default Vec3F rotateYawF(float yaw) {
		float f = FastMath.cosDegr(yaw);
		float f1 = FastMath.sinDegr(yaw);
		float d0 = this.xf() * f + this.zf() * f1;
		float d2 = this.zf() * f - this.xf() * f1;
		return new Vec3F(d0, this.yf(), d2);
	}

	default Vec3F rotateRollF(float roll) {
		float f = FastMath.cosDegr(roll);
		float f1 = FastMath.sinDegr(roll);
		float d0 = this.xf() * f + this.yf() * f1;
		float d1 = this.yf() * f - this.xf() * f1;
		return new Vec3F(d0, d1, this.zf());
	}

	default double projOn(Vec3 vec) {
		double l = vec.length();
		double ret = this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z();
		return l == 0 ? ret : ret / l;
	}

	default double projOn(double x2, double y2, double z2) {
		double l = length(x2, y2, z2);
		double ret = this.x() * x2 + this.y() * y2 + this.z() * z2;
		return l == 0 ? ret : ret / l;
	}

	default int projOnI(Vec3 vec) {
		int l = vec.lengthI();
		int ret = this.xi() * vec.xi() + this.yi() * vec.yi() + this.zi() * vec.zi();
		return l == 0 ? ret : ret / l;
	}

	default int projOn(int x2, int y2, int z2) {
		int l = lengthI(x2, y2, z2);
		int ret = this.xi() * x2 + this.yi() * y2 + this.zi() * z2;
		return l == 0 ? ret : ret / l;
	}

	default float projOnF(Vec3 vec) {
		float l = vec.lengthF();
		float ret = this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf();
		return l == 0 ? ret : ret / l;
	}

	default float projOnF(float x2, float y2, float z2) {
		float l = lengthF(x2, y2, z2);
		float ret = this.xf() * x2 + this.yf() * y2 + this.zf() * z2;
		return l == 0 ? ret : ret / l;
	}

	default Vec3 up() {
		return add(0, 1, 0);
	}

	default Vec3 down() {
		return add(0, -1, 0);
	}

	default Vec3 left() {
		return add(1, 0, 0);
	}

	default Vec3 right() {
		return add(-1, 0, 0);
	}

	default Vec3 forward() {
		return add(0, 0, 1);
	}

	default Vec3 backward() {
		return add(0, 0, -1);
	}

	default Vec3 up(int i) {
		return add(0, i, 0);
	}

	default Vec3 down(int i) {
		return add(0, -i, 0);
	}

	default Vec3 left(int i) {
		return add(i, 0, 0);
	}

	default Vec3 right(int i) {
		return add(-i, 0, 0);
	}

	default Vec3 forward(int i) {
		return add(0, 0, i);
	}

	default Vec3 backward(int i) {
		return add(0, 0, -i);
	}

	default Vec3 up(double i) {
		return add(0, i, 0);
	}

	default Vec3 down(double i) {
		return add(0, -i, 0);
	}

	default Vec3 left(double i) {
		return add(i, 0, 0);
	}

	default Vec3 right(double i) {
		return add(-i, 0, 0);
	}

	default Vec3 forward(double i) {
		return add(0, 0, i);
	}

	default Vec3 backward(double i) {
		return add(0, 0, -i);
	}

	default Vec3I upI(int i) {
		return addI(0, i, 0);
	}

	default Vec3I downI(int i) {
		return addI(0, -i, 0);
	}

	default Vec3I leftI(int i) {
		return addI(i, 0, 0);
	}

	default Vec3I rightI(int i) {
		return addI(-i, 0, 0);
	}

	default Vec3I forwardI(int i) {
		return addI(0, 0, i);
	}

	default Vec3I backwardI(int i) {
		return addI(0, 0, -i);
	}

	default Vec3F upF(float i) {
		return addF(0, i, 0);
	}

	default Vec3F downF(float i) {
		return addF(0, -i, 0);
	}

	default Vec3F leftF(float i) {
		return addF(i, 0, 0);
	}

	default Vec3F rightF(float i) {
		return addF(-i, 0, 0);
	}

	default Vec3F forwardF(float i) {
		return addF(0, 0, i);
	}

	default Vec3F backwardF(float i) {
		return addF(0, 0, -i);
	}

	default Vec3D upD(double i) {
		return add(0, i, 0);
	}

	default Vec3D downD(double i) {
		return add(0, -i, 0);
	}

	default Vec3D leftD(double i) {
		return add(i, 0, 0);
	}

	default Vec3D rightD(double i) {
		return add(-i, 0, 0);
	}

	default Vec3D forwardD(double i) {
		return add(0, 0, i);
	}

	default Vec3D backwardD(double i) {
		return add(0, 0, -i);
	}

	default Vec3D add(Vec3 vec) {
		return new Vec3D(
				this.x() + vec.x(),
				this.y() + vec.y(),
				this.z() + vec.z()
		);
	}

	default Vec3D add(double x2, double y2, double z2) {
		return new Vec3D(
				this.x() + x2,
				this.y() + y2,
				this.z() + z2
		);
	}

	default Vec3I addI(Vec3 vec) {
		return new Vec3I(
				this.xi() + vec.xi(),
				this.yi() + vec.yi(),
				this.zi() + vec.zi()
		);
	}

	default Vec3I addI(int x2, int y2, int z2) {
		return new Vec3I(
				this.xi() + x2,
				this.yi() + y2,
				this.zi() + z2
		);
	}

	default Vec3F addF(Vec3 vec) {
		return new Vec3F(
				this.xf() + vec.xf(),
				this.yf() + vec.yf(),
				this.zf() + vec.zf()
		);
	}

	default Vec3F addF(float x2, float y2, float z2) {
		return new Vec3F(
				this.xf() + x2,
				this.yf() + y2,
				this.zf() + z2
		);
	}

	default Vec3D scale(Vec3 scale) {
		return new Vec3D(
				this.x() * scale.x(),
				this.y() * scale.y(),
				this.z() * scale.z()
		);
	}

	default Vec3D scale(double sx, double sy, double sz) {
		return new Vec3D(
				this.x() * sx,
				this.y() * sy,
				this.z() * sz
		);
	}

	default Vec3D scale(double scale) {
		return new Vec3D(
				this.x() * scale,
				this.y() * scale,
				this.z() * scale
		);
	}

	default Vec3I scaleI(Vec3 scale) {
		return new Vec3I(
				this.xi() * scale.xi(),
				this.yi() * scale.yi(),
				this.zi() * scale.zi()
		);
	}

	default Vec3I scaleI(int sx, int sy, int sz) {
		return new Vec3I(
				this.xi() * sx,
				this.yi() * sy,
				this.zi() * sz
		);
	}

	default Vec3I scaleI(int scale) {
		return new Vec3I(
				this.xi() * scale,
				this.yi() * scale,
				this.zi() * scale
		);
	}

	default Vec3F scaleF(Vec3 scale) {
		return new Vec3F(
				this.xf() * scale.xf(),
				this.yf() * scale.yf(),
				this.zf() * scale.zf()
		);
	}

	default Vec3F scaleF(float sx, float sy, float sz) {
		return new Vec3F(
				this.xf() * sx,
				this.yf() * sy,
				this.zf() * sz
		);
	}

	default Vec3F scaleF(float scale) {
		return new Vec3F(
				this.xf() * scale,
				this.yf() * scale,
				this.zf() * scale
		);
	}

	default Vec3D div(Vec3 div) {
		return new Vec3D(
				this.x() / div.x(),
				this.y() / div.y(),
				this.z() / div.z()
		);
	}

	default Vec3D div(double sx, double sy, double sz) {
		return new Vec3D(
				this.x() / sx,
				this.y() / sy,
				this.z() / sz
		);
	}

	default Vec3D div(double div) {
		return new Vec3D(
				this.x() / div,
				this.y() / div,
				this.z() / div
		);
	}

	default Vec3I divI(Vec3 div) {
		return new Vec3I(
				this.xi() / div.xi(),
				this.yi() / div.yi(),
				this.zi() / div.zi()
		);
	}

	default Vec3I divI(int sx, int sy, int sz) {
		return new Vec3I(
				this.xi() / sx,
				this.yi() / sy,
				this.zi() / sz
		);
	}

	default Vec3I divI(int div) {
		return new Vec3I(
				this.xi() / div,
				this.yi() / div,
				this.zi() / div
		);
	}

	default Vec3F divF(Vec3 div) {
		return new Vec3F(
				this.xf() / div.xf(),
				this.yf() / div.yf(),
				this.zf() / div.zf()
		);
	}

	default Vec3F divF(float sx, float sy, float sz) {
		return new Vec3F(
				this.xf() / sx,
				this.yf() / sy,
				this.zf() / sz
		);
	}

	default Vec3F divF(float div) {
		return new Vec3F(
				this.xf() / div,
				this.yf() / div,
				this.zf() / div
		);
	}

	static Vec3D add(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new Vec3D(x1 + x2, y1 + y2, z1 + z2);
	}

	static Vec3I addI(int x1, int y1, int z1, int x2, int y2, int z2) {
		return new Vec3I(x1 + x2, y1 + y2, z1 + z2);
	}

	static Vec3F addF(float x1, float y1, float z1, float x2, float y2, float z2) {
		return new Vec3F(x1 + x2, y1 + y2, z1 + z2);
	}

	default Vec3D addScale(Vec3 vec, double scale) {
		return new Vec3D(
				this.x() + vec.x() * scale,
				this.y() + vec.y() * scale,
				this.z() + vec.z() * scale
		);
	}

	default Vec3D addScale(double x2, double y2, double z2, double scale) {
		return new Vec3D(
				this.x() + x2 * scale,
				this.y() + y2 * scale,
				this.z() + z2 * scale
		);
	}

	default Vec3I addScaleI(Vec3 vec, int scale) {
		return new Vec3I(
				this.xi() + vec.xi() * scale,
				this.yi() + vec.yi() * scale,
				this.zi() + vec.zi() * scale
		);
	}

	default Vec3I addScaleI(int x2, int y2, int z2, int scale) {
		return new Vec3I(
				this.xi() + x2 * scale,
				this.yi() + y2 * scale,
				this.zi() + z2 * scale
		);
	}

	default Vec3F addScaleF(Vec3 vec, float scale) {
		return new Vec3F(
				this.xf() + vec.xf() * scale,
				this.yf() + vec.yf() * scale,
				this.zf() + vec.zf() * scale
		);
	}

	default Vec3F addScaleF(float x2, float y2, float z2, float scale) {
		return new Vec3F(
				this.xf() + x2 * scale,
				this.yf() + y2 * scale,
				this.zf() + z2 * scale
		);
	}

	default Vec3D sub(Vec3 vec) {
		return new Vec3D(
				this.x() - vec.x(),
				this.y() - vec.y(),
				this.z() - vec.z()
		);
	}

	default Vec3D invSub(Vec3 vec) {
		return new Vec3D(
				-this.x() - vec.x(),
				-this.y() - vec.y(),
				-this.z() - vec.z()
		);
	}

	default Vec3D sub(double x2, double y2, double z2) {
		return new Vec3D(
				this.x() - x2,
				this.y() - y2,
				this.z() - z2
		);
	}

	default Vec3I subI(Vec3 vec) {
		return new Vec3I(
				this.xi() - vec.xi(),
				this.yi() - vec.yi(),
				this.zi() - vec.zi()
		);
	}

	default Vec3I invSubI(Vec3 vec) {
		return new Vec3I(
				-this.xi() - vec.xi(),
				-this.yi() - vec.yi(),
				-this.zi() - vec.zi()
		);
	}

	default Vec3I subI(int x2, int y2, int z2) {
		return new Vec3I(
				this.xi() - x2,
				this.yi() - y2,
				this.zi() - z2
		);
	}

	default Vec3F subF(Vec3 vec) {
		return new Vec3F(
				this.xf() - vec.xf(),
				this.yf() - vec.yf(),
				this.zf() - vec.zf()
		);
	}

	default Vec3F invSubF(Vec3 vec) {
		return new Vec3F(
				-this.xf() - vec.xf(),
				-this.yf() - vec.yf(),
				-this.zf() - vec.zf()
		);
	}

	default Vec3F subF(float x2, float y2, float z2) {
		return new Vec3F(
				this.xf() - x2,
				this.yf() - y2,
				this.zf() - z2
		);
	}

	static Vec3D sub(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new Vec3D(x1 - x2, y1 - y2, z1 - z2);
	}

	static Vec3I subI(int x1, int y1, int z1, int x2, int y2, int z2) {
		return new Vec3I(x1 - x2, y1 - y2, z1 - z2);
	}

	static Vec3F subF(float x1, float y1, float z1, float x2, float y2, float z2) {
		return new Vec3F(x1 - x2, y1 - y2, z1 - z2);
	}

	// TODO для Vec2
	default Vec3D cross(Vec3 vec) {
		return new Vec3D(
				this.y() * vec.z() - this.z() * vec.y(),
				this.z() * vec.x() - this.x() * vec.z(),
				this.x() * vec.y() - this.y() * vec.x()
		);
	}

	default Vec3D cross(double x2, double y2, double z2) {
		return new Vec3D(
				this.y() * z2 - this.z() * y2,
				this.z() * x2 - this.x() * z2,
				this.x() * y2 - this.y() * x2
		);
	}

	default Vec3I crossI(Vec3 vec) {
		return new Vec3I(
				this.yi() * vec.zi() - this.zi() * vec.yi(),
				this.zi() * vec.xi() - this.xi() * vec.zi(),
				this.xi() * vec.yi() - this.yi() * vec.xi()
		);
	}

	default Vec3I crossI(int x2, int y2, int z2) {
		return new Vec3I(
				this.yi() * z2 - this.zi() * y2,
				this.zi() * x2 - this.xi() * z2,
				this.xi() * y2 - this.yi() * x2
		);
	}

	default Vec3F crossF(Vec3 vec) {
		return new Vec3F(
				this.yf() * vec.zf() - this.zf() * vec.yf(),
				this.zf() * vec.xf() - this.xf() * vec.zf(),
				this.xf() * vec.yf() - this.yf() * vec.xf()
		);
	}

	default Vec3F crossF(float x2, float y2, float z2) {
		return new Vec3F(
				this.yf() * z2 - this.zf() * y2,
				this.zf() * x2 - this.xf() * z2,
				this.xf() * y2 - this.yf() * x2
		);
	}

	static Vec3D cross(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new Vec3D(
				y1 * z2 - z1 * y2,
				z1 * x2 - x1 * z2,
				x1 * y2 - y1 * x2
		);
	}

	static Vec3I crossI(int x1, int y1, int z1, int x2, int y2, int z2) {
		return new Vec3I(
				y1 * z2 - z1 * y2,
				z1 * x2 - x1 * z2,
				x1 * y2 - y1 * x2
		);
	}

	static Vec3F crossF(float x1, float y1, float z1, float x2, float y2, float z2) {
		return new Vec3F(
				y1 * z2 - z1 * y2,
				z1 * x2 - x1 * z2,
				x1 * y2 - y1 * x2
		);
	}

	default Vec3D lerp(Vec3 to, double part) {
		final double x = this.x() + (to.x() - this.x()) * part;
		final double y = this.y() + (to.y() - this.y()) * part;
		final double z = this.z() + (to.z() - this.z()) * part;
		return new Vec3D(x, y, z);
	}

	default Vec3D lerp(double toX, double toY, double toZ, double part) {
		final double x = this.x() + (toX - this.x()) * part;
		final double y = this.y() + (toY - this.y()) * part;
		final double z = this.z() + (toZ - this.z()) * part;
		return new Vec3D(x, y, z);
	}

	default Vec3F lerpF(Vec3 to, float part) {
		final float x = this.xf() + (to.xf() - this.xf()) * part;
		final float y = this.yf() + (to.yf() - this.yf()) * part;
		final float z = this.zf() + (to.zf() - this.zf()) * part;
		return new Vec3F(x, y, z);
	}

	default Vec3F lerpF(float toX, float toY, float toZ, float part) {
		final float x = this.xf() + (toX - this.xf()) * part;
		final float y = this.yf() + (toY - this.yf()) * part;
		final float z = this.zf() + (toZ - this.zf()) * part;
		return new Vec3F(x, y, z);
	}

	default Vec3D transform(Matrix3 matrixIn) {
		final double x = matrixIn.m00() * this.x() + matrixIn.m01() * this.y() + matrixIn.m02() * this.z();
		final double y = matrixIn.m10() * this.x() + matrixIn.m11() * this.y() + matrixIn.m12() * this.z();
		final double z = matrixIn.m20() * this.x() + matrixIn.m21() * this.y() + matrixIn.m22() * this.z();
		return new Vec3D(x, y, z);
	}

	default Vec3F transformF(Matrix3 matrixIn) {
		final float x = matrixIn.m00f() * this.xf() + matrixIn.m01f() * this.yf() + matrixIn.m02f() * this.zf();
		final float y = matrixIn.m10f() * this.xf() + matrixIn.m11f() * this.yf() + matrixIn.m12f() * this.zf();
		final float z = matrixIn.m20f() * this.xf() + matrixIn.m21f() * this.yf() + matrixIn.m22f() * this.zf();
		return new Vec3F(x, y, z);
	}

	default Vec3D transform(Quat quaternionIn) {
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
		return new Vec3D(nx, ny, nz);
	}

	default Vec3F transformF(Quat quaternionIn) {
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
		return new Vec3F(nx, ny, nz);
	}

	default Vec3D clamp(double min, double max) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		if (x > max) {
			x = max;
		} else if (x < min) {
			x = min;
		}
		if (y > max) {
			y = max;
		} else if (y < min) {
			y = min;
		}
		if (z > max) {
			z = max;
		} else if (z < min) {
			z = min;
		}
		return new Vec3D(x, y, z);
	}

	default Vec3I clampI(int min, int max) {
		int x = this.xi();
		int y = this.yi();
		int z = this.zi();
		if (x > max) {
			x = max;
		} else if (x < min) {
			x = min;
		}
		if (y > max) {
			y = max;
		} else if (y < min) {
			y = min;
		}
		if (z > max) {
			z = max;
		} else if (z < min) {
			z = min;
		}
		return new Vec3I(x, y, z);
	}

	default Vec3F clampF(float min, float max) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		if (x > max) {
			x = max;
		} else if (x < min) {
			x = min;
		}
		if (y > max) {
			y = max;
		} else if (y < min) {
			y = min;
		}
		if (z > max) {
			z = max;
		} else if (z < min) {
			z = min;
		}
		return new Vec3F(x, y, z);
	}


	default Vec3D flip() {
		return new Vec3D(
				1d / this.x(),
				1d / this.y(),
				1d / this.z()
		);
	}

	default Vec3F flipF() {
		return new Vec3F(
				1f / this.xf(),
				1f / this.yf(),
				1f / this.zf()
		);
	}

	default Vec3D abs() {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		if (x < 0) {
			x = -x;
		}
		if (y < 0) {
			y = -y;
		}
		if (z < 0) {
			z = -z;
		}
		return new Vec3D(x, y, z);
	}

	default Vec3I absI() {
		int x = this.xi();
		int y = this.yi();
		int z = this.zi();
		if (x < 0) {
			x = -x;
		}
		if (y < 0) {
			y = -y;
		}
		if (z < 0) {
			z = -z;
		}
		return new Vec3I(x, y, z);
	}

	default Vec3F absF() {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		if (x < 0) {
			x = -x;
		}
		if (y < 0) {
			y = -y;
		}
		if (z < 0) {
			z = -z;
		}
		return new Vec3F(x, y, z);
	}

	default Vec3D greater(double d) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		if (x < d) {
			x = d;
		}
		if (y < d) {
			y = d;
		}
		if (z < d) {
			z = d;
		}
		return new Vec3D(x, y, z);
	}

	default Vec3I greaterI(int d) {
		int x = this.xi();
		int y = this.yi();
		int z = this.zi();
		if (x < d) {
			x = d;
		}
		if (y < d) {
			y = d;
		}
		if (z < d) {
			z = d;
		}
		return new Vec3I(x, y, z);
	}

	default Vec3F greaterF(float d) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		if (x < d) {
			x = d;
		}
		if (y < d) {
			y = d;
		}
		if (z < d) {
			z = d;
		}
		return new Vec3F(x, y, z);
	}

	default Vec3D less(double d) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		if (x > d) {
			x = d;
		}
		if (y > d) {
			y = d;
		}
		if (z > d) {
			z = d;
		}
		return new Vec3D(x, y, z);
	}

	default Vec3I lessI(int d) {
		int x = this.xi();
		int y = this.yi();
		int z = this.zi();
		if (x > d) {
			x = d;
		}
		if (y > d) {
			y = d;
		}
		if (z > d) {
			z = d;
		}
		return new Vec3I(x, y, z);
	}

	default Vec3F lessF(float d) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		if (x > d) {
			x = d;
		}
		if (y > d) {
			y = d;
		}
		if (z > d) {
			z = d;
		}
		return new Vec3F(x, y, z);
	}

	default Vec3D aprZeroComp(double lim) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		if (Math.abs(x) < lim) {
			x = 0;
		}
		if (Math.abs(y) < lim) {
			y = 0;
		}
		if (Math.abs(z) < lim) {
			z = 0;
		}

		if (x == 0 && y == 0 && z == 0) return Vec3D.ZERO;
		return new Vec3D(x, y, z);
	}

	default Vec3F aprZeroCompF(double lim) {
		float x = this.xf();
		float y = this.yf();
		float z = this.zf();
		if (Math.abs(x) < lim) {
			x = 0;
		}
		if (Math.abs(y) < lim) {
			y = 0;
		}
		if (Math.abs(z) < lim) {
			z = 0;
		}

		if (x == 0 && y == 0 && z == 0) return Vec3F.ZERO;
		return new Vec3F(x, y, z);
	}

	default Vec3 aprZeroAll(double lim) {
		double x = this.x();
		double y = this.y();
		double z = this.z();
		if (Math.abs(x) < lim && Math.abs(y) < lim && Math.abs(z) < lim) {
			return Vec3D.ZERO;
		}
		return this;
	}

	static Vec3D avg(Vec3... vectors) {
		int c = 0;

		double x = 0;
		double y = 0;
		double z = 0;
		for (Vec3 vec : vectors) {
			if (vec != null) {
				c++;
				x += vec.x();
				y += vec.y();
				z += vec.z();
			}
		}

		if (c == 0) {
			return Vec3D.ZERO;
		}

		return new Vec3D(x / c, y / c, z / c);
	}

	static boolean equals(Vec3 v1, Vec3 v2) {
		if (v1 == v2) {
			return true;
		} else if ((v1 == null) != (v2 == null)) {
			return false;
		} else {
			if (v1.x() != v2.x()) {
				return false;
			} else if (v1.y() != v2.y()) {
				return false;
			} else {
				return v1.z() == v2.z();
			}
		}
	}

	static int hashCode(Vec3 vec) {
		int i = Double.hashCode(vec.x());
		i = 31 * i + Double.hashCode(vec.y());
		return 31 * i + Double.hashCode(vec.z());
	}

	default double[] asArray() {
		return new double[]{x(), y(), z()};
	}

	default float[] asArrayF() {
		return new float[]{xf(), yf(), zf()};
	}

	default Vec3D xzy() {
		return new Vec3D(x(), z(), y());
	}

	default Vec3F xzyF() {
		return new Vec3F(xf(), zf(), yf());
	}

	default Vec3D zxy() {
		return new Vec3D(z(), x(), y());
	}

	default Vec3F zxyF() {
		return new Vec3F(zf(), xf(), yf());
	}

	default Vec3D zyx() {
		return new Vec3D(z(), y(), x());
	}

	default Vec3F zyxF() {
		return new Vec3F(zf(), yf(), xf());
	}

	default Vec3D yxz() {
		return new Vec3D(y(), x(), z());
	}

	default Vec3F yxzF() {
		return new Vec3F(yf(), xf(), zf());
	}

	default Vec3D yzc() {
		return new Vec3D(y(), z(), x());
	}

	default Vec3F yzcF() {
		return new Vec3F(yf(), zf(), xf());
	}

	default Vec2D xy() {
		return new Vec2D(x(), y());
	}

	default Vec2F xyF() {
		return new Vec2F(xf(), yf());
	}

	default Vec2D xz() {
		return new Vec2D(x(), z());
	}

	default Vec2F xzF() {
		return new Vec2F(xf(), zf());
	}

	default Vec2D yx() {
		return new Vec2D(y(), x());
	}

	default Vec2F yxF() {
		return new Vec2F(yf(), xf());
	}

	default Vec2D yz() {
		return new Vec2D(y(), z());
	}

	default Vec2F yzF() {
		return new Vec2F(yf(), zf());
	}

	default Vec2D zx() {
		return new Vec2D(z(), x());
	}

	default Vec2F zxF() {
		return new Vec2F(zf(), xf());
	}

	default Vec2D zy() {
		return new Vec2D(z(), y());
	}

	default Vec2F zyF() {
		return new Vec2F(zf(), yf());
	}

	@Override
	default Vec3I getAsIntVec() {
		return new Vec3I(this.xi(), this.yi(), this.zi());
	}

	@Override
	default Vec3F getAsFloatVec() {
		return new Vec3F(this.xf(), this.yf(), this.zf());
	}

	@Override
	default Vec3D getAsDoubleVec() {
		return new Vec3D(this.x(), this.y(), this.z());
	}

	static final class JCodec extends AbstractJsonCodec<Vec3> {

		private final JsonSerializer<Vec3I> veci = this.registry.getSerializerIndirect(Vec3I.class);
		private final JsonSerializer<Vec3> vecd = this.registry.getSerializerIndirect(Vec3D.class);

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Vec3 value, JsonWriter writer) throws IOException {
			if (value instanceof Vec3I vec) {
				this.veci.write(vec, writer);
			} else {
				this.vecd.write(value, writer);
			}
		}

		@Override
		public Vec3 read(JsonReader reader) throws IOException {
			Number x = 0;
			Number y = 0;
			Number z = 0;
			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					x = reader.readNumber();
					y = reader.readNumber();
					z = reader.readNumber();
					reader.endArray();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
						String s = reader.readName();
						Number i = reader.readNumber();
						switch (s.toLowerCase()) {
							case "x" -> x = i;
							case "y" -> y = i;
							case "z" -> z = i;
						}
					}
					reader.endObject();
				}
				case NUMBER -> {
					Number value = reader.readNumber();
					x = value;
					y = value;
					z = value;
				}
				default ->
						throw new JsonReadException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}
			if (x instanceof Double || y instanceof Double || z instanceof Double) {
				return new Vec3D(x.doubleValue(), y.doubleValue(), z.doubleValue());
			} else {
				return new Vec3I(x.intValue(), y.intValue(), z.intValue());
			}
		}
	}

	static int compareTo(Vec3 a, Vec3 b) {
		if ((a instanceof Vec3I || a instanceof Direction) && (b instanceof Vec3I || b instanceof Direction)) {
			if (a.xi() > b.xi()) {
				return 1;
			}
			if (a.xi() < b.xi()) {
				return -1;
			}
			if (a.yi() > b.yi()) {
				return 1;
			}
			if (a.yi() < b.yi()) {
				return -1;
			}
			return Integer.compare(a.zi(), b.zi());
		} else {
			if (a.x() > b.x()) {
				return 1;
			}
			if (a.x() < b.x()) {
				return -1;
			}
			if (a.y() > b.y()) {
				return 1;
			}
			if (a.y() < b.y()) {
				return -1;
			}
			return Double.compare(a.z(), b.z());
		}
	}
}
