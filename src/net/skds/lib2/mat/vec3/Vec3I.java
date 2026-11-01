package net.skds.lib2.mat.vec3;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.mat.FastMath;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.skds.lib2.io.sosison.SosisonEntryType.END_OBJECT;

@SuppressWarnings("unused")
@DefaultCodec(Vec3I.Codec.class)
public record Vec3I(int xi, int yi, int zi) implements Vec3, Comparable<Vec3> {

	public static final Vec3I XN = new Vec3I(-1, 0, 0);
	public static final Vec3I XP = new Vec3I(1, 0, 0);
	public static final Vec3I YN = new Vec3I(0, -1, 0);
	public static final Vec3I YP = new Vec3I(0, 1, 0);
	public static final Vec3I ZN = new Vec3I(0, 0, -1);
	public static final Vec3I ZP = new Vec3I(0, 0, 1);
	public static final Vec3I SINGLE = new Vec3I(1, 1, 1);
	public static final Vec3I ZERO = new Vec3I(0, 0, 0);

	@Override
	public double x() {
		return xi;
	}

	@Override
	public double y() {
		return yi;
	}

	@Override
	public double z() {
		return zi;
	}

	@Override
	public float xf() {
		return xi;
	}

	@Override
	public float yf() {
		return yi;
	}

	@Override
	public float zf() {
		return zi;
	}

	@Override
	public int floorX() {
		return xi;
	}

	@Override
	public int floorY() {
		return yi;
	}

	@Override
	public int floorZ() {
		return zi;
	}

	@Override
	public int ceilX() {
		return xi;
	}

	@Override
	public int ceilY() {
		return yi;
	}

	@Override
	public int ceilZ() {
		return zi;
	}

	@Override
	public int roundX() {
		return xi;
	}

	@Override
	public int roundY() {
		return yi;
	}

	@Override
	public int roundZ() {
		return zi;
	}

	public Vec3I(Vec3 vec) {
		this(vec.floorX(), vec.floorY(), vec.floorZ());
	}

	public Vec3I(int size) {
		this(size, size, size);
	}

	public Vec3I(double x, double y, double z) {
		this(FastMath.floor(x), FastMath.floor(y), FastMath.floor(z));
	}

	@Override
	public Vec3I up() {
		return addI(0, 1, 0);
	}

	@Override
	public Vec3I down() {
		return addI(0, -1, 0);
	}

	@Override
	public Vec3I left() {
		return addI(1, 0, 0);
	}

	@Override
	public Vec3I right() {
		return addI(-1, 0, 0);
	}

	@Override
	public Vec3I forward() {
		return addI(0, 0, 1);
	}

	@Override
	public Vec3I backward() {
		return addI(0, 0, -1);
	}

	@Override
	public Vec3I up(int i) {
		return addI(0, i, 0);
	}

	@Override
	public Vec3I down(int i) {
		return addI(0, -i, 0);
	}

	@Override
	public Vec3I left(int i) {
		return addI(i, 0, 0);
	}

	@Override
	public Vec3I right(int i) {
		return addI(-i, 0, 0);
	}

	@Override
	public Vec3I forward(int i) {
		return addI(0, 0, i);
	}

	@Override
	public Vec3I backward(int i) {
		return addI(0, 0, -i);
	}

	@Override
	public Vec3I up(double i) {
		return addI(0, FastMath.round(i), 0);
	}

	@Override
	public Vec3I down(double i) {
		return addI(0, FastMath.round(-i), 0);
	}

	@Override
	public Vec3I left(double i) {
		return addI(FastMath.round(i), 0, 0);
	}

	@Override
	public Vec3I right(double i) {
		return addI(FastMath.round(-i), 0, 0);
	}

	@Override
	public Vec3I forward(double i) {
		return addI(0, 0, FastMath.round(i));
	}

	@Override
	public Vec3I backward(double i) {
		return addI(0, 0, FastMath.round(-i));
	}

	public Vec3 getCenter() {
		return Vec3.of(this.xi() + 0.5, this.yi() + 0.5, this.zi() + 0.5);
	}

	public double distanceTo(Vec3D vec) {
		return Math.sqrt((vec.x() - this.x()) * (vec.x() - this.x()) + (vec.y() - this.y()) * (vec.y() - this.y()) + (vec.z() - this.z()) * (vec.z() - this.z()));
	}

	public double distanceTo(Vec3F vec) {
		return Math.sqrt((vec.x() - this.x()) * (vec.x() - this.x()) + (vec.y() - this.y()) * (vec.y() - this.y()) + (vec.z() - this.z()) * (vec.z() - this.z()));
	}

	public double distanceTo(Vec3I vec) {
		return Math.sqrt((vec.x() - this.x()) * (vec.x() - this.x()) + (vec.y() - this.y()) * (vec.y() - this.y()) + (vec.z() - this.z()) * (vec.z() - this.z()));
	}

	public double squareDistanceTo(Vec3D vec) {
		return (vec.x() - this.x()) * (vec.x() - this.x()) + (vec.y() - this.y()) * (vec.y() - this.y()) + (vec.z() - this.z()) * (vec.z() - this.z());
	}

	public double squareDistanceTo(Vec3F vec) {
		return (vec.x() - this.x()) * (vec.x() - this.x()) + (vec.y() - this.y()) * (vec.y() - this.y()) + (vec.z() - this.z()) * (vec.z() - this.z());
	}

	public double squareDistanceTo(Vec3I vec) {
		return (vec.x() - this.x()) * (vec.x() - this.x()) + (vec.y() - this.y()) * (vec.y() - this.y()) + (vec.z() - this.z()) * (vec.z() - this.z());
	}

	public float distanceToF(Vec3D vec) {
		return (float) Math.sqrt((vec.xf() - this.xf()) * (vec.xf() - this.xf()) + (vec.yf() - this.yf()) * (vec.yf() - this.yf()) + (vec.zf() - this.zf()) * (vec.zf() - this.zf()));
	}

	public float distanceToF(Vec3F vec) {
		return (float) Math.sqrt((vec.xf() - this.xf()) * (vec.xf() - this.xf()) + (vec.yf() - this.yf()) * (vec.yf() - this.yf()) + (vec.zf() - this.zf()) * (vec.zf() - this.zf()));
	}

	public float distanceToF(Vec3I vec) {
		return (float) Math.sqrt((vec.xf() - this.xf()) * (vec.xf() - this.xf()) + (vec.yf() - this.yf()) * (vec.yf() - this.yf()) + (vec.zf() - this.zf()) * (vec.zf() - this.zf()));
	}

	public float squareDistanceToF(Vec3D vec) {
		return (vec.xf() - this.xf()) * (vec.xf() - this.xf()) + (vec.yf() - this.yf()) * (vec.yf() - this.yf()) + (vec.zf() - this.zf()) * (vec.zf() - this.zf());
	}

	public float squareDistanceToF(Vec3F vec) {
		return (vec.xf() - this.xf()) * (vec.xf() - this.xf()) + (vec.yf() - this.yf()) * (vec.yf() - this.yf()) + (vec.zf() - this.zf()) * (vec.zf() - this.zf());
	}

	public float squareDistanceToF(Vec3I vec) {
		return (vec.xf() - this.xf()) * (vec.xf() - this.xf()) + (vec.yf() - this.yf()) * (vec.yf() - this.yf()) + (vec.zf() - this.zf()) * (vec.zf() - this.zf());
	}

	@Override
	public Vec3D normalize() {
		double x = xi;
		double y = yi;
		double z = zi;
		double k = FastMath.invSqrt(x * x + y * y + z * z + Double.MIN_NORMAL);
		return new Vec3D(x * k, y * k, z * k);
	}

	@Override
	public Direction normalizeI() {
		return Direction.getFacing(xi, yi, zi);
	}

	@Override
	public Vec3F normalizeF() {
		float x = xi;
		float y = yi;
		float z = zi;
		float k = FastMath.invSqrt(x * x + y * y + z * z + Float.MIN_NORMAL);
		return new Vec3F(x * k, y * k, z * k);
	}

	@Override
	public Vec3D normalizeScale(double scale) {
		if (scale == 0) return Vec3D.ZERO;
		double x = xi;
		double y = yi;
		double z = zi;
		double k = FastMath.invSqrt(x * x + y * y + z * z + Double.MIN_NORMAL) * scale;
		return new Vec3D(x * k, y * k, z * k);
	}

	@Override
	public Vec3I normalizeScaleI(int scale) {
		if (scale == 0) return Vec3I.ZERO;
		return Direction.getFacing(xi, yi, zi).scaleI(scale);
	}

	@Override
	public Vec3F normalizeScaleF(float scale) {
		if (scale == 0) return Vec3F.ZERO;
		float x = xi;
		float y = yi;
		float z = zi;
		float k = FastMath.invSqrt(x * x + y * y + z * z + Float.MIN_NORMAL) * scale;
		return new Vec3F(x * k, y * k, z * k);
	}

	public double dot(Vec3D vec) {
		return this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z();
	}

	public double dot(Vec3F vec) {
		return this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z();
	}

	public double dot(Vec3I vec) {
		return this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z();
	}

	public int dotI(Vec3D vec) {
		return this.xi() * vec.xi() + this.yi() * vec.yi() + this.zi() * vec.zi();
	}

	public int dotI(Vec3F vec) {
		return this.xi() * vec.xi() + this.yi() * vec.yi() + this.zi() * vec.zi();
	}

	public int dotI(Vec3I vec) {
		return this.xi() * vec.xi() + this.yi() * vec.yi() + this.zi() * vec.zi();
	}

	public float dotF(Vec3D vec) {
		return this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf();
	}

	public float dotF(Vec3F vec) {
		return this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf();
	}

	public float dotF(Vec3I vec) {
		return this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf();
	}

	public double projOn(Vec3D vec) {
		double k = FastMath.invSqrt((vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z() + Double.MIN_NORMAL));
		return (this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z()) * k;
	}

	public double projOn(Vec3F vec) {
		double k = FastMath.invSqrt((vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z() + Double.MIN_NORMAL));
		return (this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z()) * k;
	}

	public double projOn(Vec3I vec) {
		double k = FastMath.invSqrt((vec.x() * vec.x() + vec.y() * vec.y() + vec.z() * vec.z() + Double.MIN_NORMAL));
		return (this.x() * vec.x() + this.y() * vec.y() + this.z() * vec.z()) * k;
	}


	public float projOnF(Vec3D vec) {
		float k = FastMath.invSqrt((vec.xf() * vec.xf() + vec.yf() * vec.yf() + vec.zf() * vec.zf() + Float.MIN_NORMAL));
		return (this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf()) * k;
	}

	public float projOnF(Vec3F vec) {
		float k = FastMath.invSqrt((vec.xf() * vec.xf() + vec.yf() * vec.yf() + vec.zf() * vec.zf() + Float.MIN_NORMAL));
		return (this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf()) * k;
	}

	public float projOnF(Vec3I vec) {
		float k = FastMath.invSqrt((vec.xf() * vec.xf() + vec.yf() * vec.yf() + vec.zf() * vec.zf() + Float.MIN_NORMAL));
		return (this.xf() * vec.xf() + this.yf() * vec.yf() + this.zf() * vec.zf()) * k;
	}

	public Vec3D add(Vec3D vec) {
		return new Vec3D(this.x() + vec.x(), this.y() + vec.y(), this.z() + vec.z());
	}

	public Vec3D add(Vec3F vec) {
		return new Vec3D(this.x() + vec.x(), this.y() + vec.y(), this.z() + vec.z());
	}

	public Vec3D add(Vec3I vec) {
		return new Vec3D(this.x() + vec.x(), this.y() + vec.y(), this.z() + vec.z());
	}

	public Vec3I addI(Vec3D vec) {
		return new Vec3I(this.xi() + vec.xi(), this.yi() + vec.yi(), this.zi() + vec.zi());
	}

	public Vec3I addI(Vec3F vec) {
		return new Vec3I(this.xi() + vec.xi(), this.yi() + vec.yi(), this.zi() + vec.zi());
	}

	public Vec3I addI(Vec3I vec) {
		return new Vec3I(this.xi() + vec.xi(), this.yi() + vec.yi(), this.zi() + vec.zi());
	}

	public Vec3F addF(Vec3D vec) {
		return new Vec3F(this.xf() + vec.xf(), this.yf() + vec.yf(), this.zf() + vec.zf());
	}

	public Vec3F addF(Vec3F vec) {
		return new Vec3F(this.xf() + vec.xf(), this.yf() + vec.yf(), this.zf() + vec.zf());
	}

	public Vec3F addF(Vec3I vec) {
		return new Vec3F(this.xf() + vec.xf(), this.yf() + vec.yf(), this.zf() + vec.zf());
	}

	public Vec3D scale(Vec3D vec) {
		return new Vec3D(this.x() * vec.x(), this.y() * vec.y(), this.z() * vec.z());
	}

	public Vec3D scale(Vec3F vec) {
		return new Vec3D(this.x() * vec.x(), this.y() * vec.y(), this.z() * vec.z());
	}

	public Vec3D scale(Vec3I vec) {
		return new Vec3D(this.x() * vec.x(), this.y() * vec.y(), this.z() * vec.z());
	}

	public Vec3I scaleI(Vec3D vec) {
		return new Vec3I(this.xi() * vec.xi(), this.yi() * vec.yi(), this.zi() * vec.zi());
	}

	public Vec3I scaleI(Vec3F vec) {
		return new Vec3I(this.xi() * vec.xi(), this.yi() * vec.yi(), this.zi() * vec.zi());
	}

	public Vec3I scaleI(Vec3I vec) {
		return new Vec3I(this.xi() * vec.xi(), this.yi() * vec.yi(), this.zi() * vec.zi());
	}

	public Vec3F scaleF(Vec3D vec) {
		return new Vec3F(this.xf() * vec.xf(), this.yf() * vec.yf(), this.zf() * vec.zf());
	}

	public Vec3F scaleF(Vec3F vec) {
		return new Vec3F(this.xf() * vec.xf(), this.yf() * vec.yf(), this.zf() * vec.zf());
	}

	public Vec3F scaleF(Vec3I vec) {
		return new Vec3F(this.xf() * vec.xf(), this.yf() * vec.yf(), this.zf() * vec.zf());
	}

	@Deprecated
	public Vec3D div(Vec3D vec) {
		return new Vec3D(this.x() / vec.x(), this.y() / vec.y(), this.z() / vec.z());
	}

	@Deprecated
	public Vec3D div(Vec3F vec) {
		return new Vec3D(this.x() / vec.x(), this.y() / vec.y(), this.z() / vec.z());
	}

	@Deprecated
	public Vec3D div(Vec3I vec) {
		return new Vec3D(this.x() / vec.x(), this.y() / vec.y(), this.z() / vec.z());
	}

	@Deprecated
	public Vec3I divI(Vec3D vec) {
		return new Vec3I(this.xi() / vec.xi(), this.yi() / vec.yi(), this.zi() / vec.zi());
	}

	@Deprecated
	public Vec3I divI(Vec3F vec) {
		return new Vec3I(this.xi() / vec.xi(), this.yi() / vec.yi(), this.zi() / vec.zi());
	}

	@Deprecated
	public Vec3I divI(Vec3I vec) {
		return new Vec3I(this.xi() / vec.xi(), this.yi() / vec.yi(), this.zi() / vec.zi());
	}

	@Deprecated
	public Vec3F divF(Vec3D vec) {
		return new Vec3F(this.xf() / vec.xf(), this.yf() / vec.yf(), this.zf() / vec.zf());
	}

	@Deprecated
	public Vec3F divF(Vec3F vec) {
		return new Vec3F(this.xf() / vec.xf(), this.yf() / vec.yf(), this.zf() / vec.zf());
	}

	@Deprecated
	public Vec3F divF(Vec3I vec) {
		return new Vec3F(this.xf() / vec.xf(), this.yf() / vec.yf(), this.zf() / vec.zf());
	}

	public Vec3D addScale(Vec3D vec, double scale) {
		return new Vec3D(this.x() + vec.x() * scale, this.y() + vec.y() * scale, this.z() + vec.z() * scale);
	}

	public Vec3D addScale(Vec3F vec, double scale) {
		return new Vec3D(this.x() + vec.x() * scale, this.y() + vec.y() * scale, this.z() + vec.z() * scale);
	}

	public Vec3D addScale(Vec3I vec, double scale) {
		return new Vec3D(this.x() + vec.x() * scale, this.y() + vec.y() * scale, this.z() + vec.z() * scale);
	}

	public Vec3I addScaleI(Vec3D vec, int scale) {
		return new Vec3I(this.xi() + vec.xi() * scale, this.yi() + vec.yi() * scale, this.zi() + vec.zi() * scale);
	}

	public Vec3I addScaleI(Vec3F vec, int scale) {
		return new Vec3I(this.xi() + vec.xi() * scale, this.yi() + vec.yi() * scale, this.zi() + vec.zi() * scale);
	}

	public Vec3I addScaleI(Vec3I vec, int scale) {
		return new Vec3I(this.xi() + vec.xi() * scale, this.yi() + vec.yi() * scale, this.zi() + vec.zi() * scale);
	}

	public Vec3F addScaleF(Vec3D vec, float scale) {
		return new Vec3F(this.xf() + vec.xf() * scale, this.yf() + vec.yf() * scale, this.zf() + vec.zf() * scale);
	}

	public Vec3F addScaleF(Vec3F vec, float scale) {
		return new Vec3F(this.xf() + vec.xf() * scale, this.yf() + vec.yf() * scale, this.zf() + vec.zf() * scale);
	}

	public Vec3F addScaleF(Vec3I vec, float scale) {
		return new Vec3F(this.xf() + vec.xf() * scale, this.yf() + vec.yf() * scale, this.zf() + vec.zf() * scale);
	}

	public Vec3D sub(Vec3D vec) {
		return new Vec3D(this.x() - vec.x(), this.y() - vec.y(), this.z() - vec.z());
	}

	public Vec3D sub(Vec3F vec) {
		return new Vec3D(this.x() - vec.x(), this.y() - vec.y(), this.z() - vec.z());
	}

	public Vec3D sub(Vec3I vec) {
		return new Vec3D(this.x() - vec.x(), this.y() - vec.y(), this.z() - vec.z());
	}

	public Vec3I subI(Vec3D vec) {
		return new Vec3I(this.xi() - vec.xi(), this.yi() - vec.yi(), this.zi() - vec.zi());
	}

	public Vec3I subI(Vec3F vec) {
		return new Vec3I(this.xi() - vec.xi(), this.yi() - vec.yi(), this.zi() - vec.zi());
	}

	public Vec3I subI(Vec3I vec) {
		return new Vec3I(this.xi() - vec.xi(), this.yi() - vec.yi(), this.zi() - vec.zi());
	}

	public Vec3F subF(Vec3D vec) {
		return new Vec3F(this.xf() - vec.xf(), this.yf() - vec.yf(), this.zf() - vec.zf());
	}

	public Vec3F subF(Vec3F vec) {
		return new Vec3F(this.xf() - vec.xf(), this.yf() - vec.yf(), this.zf() - vec.zf());
	}

	public Vec3F subF(Vec3I vec) {
		return new Vec3F(this.xf() - vec.xf(), this.yf() - vec.yf(), this.zf() - vec.zf());
	}

	public Vec3D invSub(Vec3D vec) {
		return new Vec3D(-this.x() - vec.x(), -this.y() - vec.y(), -this.z() - vec.z());
	}

	public Vec3D invSub(Vec3F vec) {
		return new Vec3D(-this.x() - vec.x(), -this.y() - vec.y(), -this.z() - vec.z());
	}

	public Vec3D invSub(Vec3I vec) {
		return new Vec3D(-this.x() - vec.x(), -this.y() - vec.y(), -this.z() - vec.z());
	}

	public Vec3I invSubI(Vec3D vec) {
		return new Vec3I(-this.xi() - vec.xi(), -this.yi() - vec.yi(), -this.zi() - vec.zi());
	}

	public Vec3I invSubI(Vec3F vec) {
		return new Vec3I(-this.xi() - vec.xi(), -this.yi() - vec.yi(), -this.zi() - vec.zi());
	}

	public Vec3I invSubI(Vec3I vec) {
		return new Vec3I(-this.xi() - vec.xi(), -this.yi() - vec.yi(), -this.zi() - vec.zi());
	}

	public Vec3F invSubF(Vec3D vec) {
		return new Vec3F(-this.xf() - vec.xf(), -this.yf() - vec.yf(), -this.zf() - vec.zf());
	}

	public Vec3F invSubF(Vec3F vec) {
		return new Vec3F(-this.xf() - vec.xf(), -this.yf() - vec.yf(), -this.zf() - vec.zf());
	}

	public Vec3F invSubF(Vec3I vec) {
		return new Vec3F(-this.xf() - vec.xf(), -this.yf() - vec.yf(), -this.zf() - vec.zf());
	}

	public Vec3D cross(Vec3D vec) {
		return new Vec3D(this.y() * vec.z() - this.z() * vec.y(), this.z() * vec.x() - this.x() * vec.z(), this.x() * vec.y() - this.y() * vec.x());
	}

	public Vec3D cross(Vec3F vec) {
		return new Vec3D(this.y() * vec.z() - this.z() * vec.y(), this.z() * vec.x() - this.x() * vec.z(), this.x() * vec.y() - this.y() * vec.x());
	}

	public Vec3D cross(Vec3I vec) {
		return new Vec3D(this.y() * vec.z() - this.z() * vec.y(), this.z() * vec.x() - this.x() * vec.z(), this.x() * vec.y() - this.y() * vec.x());
	}

	public Vec3I crossI(Vec3D vec) {
		return new Vec3I(this.yi() * vec.zi() - this.zi() * vec.yi(), this.zi() * vec.xi() - this.xi() * vec.zi(), this.xi() * vec.yi() - this.yi() * vec.xi());
	}

	public Vec3I crossI(Vec3F vec) {
		return new Vec3I(this.yi() * vec.zi() - this.zi() * vec.yi(), this.zi() * vec.xi() - this.xi() * vec.zi(), this.xi() * vec.yi() - this.yi() * vec.xi());
	}

	public Vec3I crossI(Vec3I vec) {
		return new Vec3I(this.yi() * vec.zi() - this.zi() * vec.yi(), this.zi() * vec.xi() - this.xi() * vec.zi(), this.xi() * vec.yi() - this.yi() * vec.xi());
	}

	public Vec3F crossF(Vec3D vec) {
		return new Vec3F(this.yf() * vec.zf() - this.zf() * vec.yf(), this.zf() * vec.xf() - this.xf() * vec.zf(), this.xf() * vec.yf() - this.yf() * vec.xf());
	}

	public Vec3F crossF(Vec3F vec) {
		return new Vec3F(this.yf() * vec.zf() - this.zf() * vec.yf(), this.zf() * vec.xf() - this.xf() * vec.zf(), this.xf() * vec.yf() - this.yf() * vec.xf());
	}

	public Vec3F crossF(Vec3I vec) {
		return new Vec3F(this.yf() * vec.zf() - this.zf() * vec.yf(), this.zf() * vec.xf() - this.xf() * vec.zf(), this.xf() * vec.yf() - this.yf() * vec.xf());
	}

	public Vec3D lerp(Vec3D vec, double part) {
		return new Vec3D(this.x() + (vec.x() - this.x()) * part, this.y() + (vec.y() - this.y()) * part, this.z() + (vec.z() - this.z()) * part);
	}

	public Vec3D lerp(Vec3F vec, double part) {
		return new Vec3D(this.x() + (vec.x() - this.x()) * part, this.y() + (vec.y() - this.y()) * part, this.z() + (vec.z() - this.z()) * part);
	}

	public Vec3D lerp(Vec3I vec, double part) {
		return new Vec3D(this.x() + (vec.x() - this.x()) * part, this.y() + (vec.y() - this.y()) * part, this.z() + (vec.z() - this.z()) * part);
	}

	public Vec3F lerpF(Vec3D vec, float part) {
		return new Vec3F(this.xf() + (vec.xf() - this.xf()) * part, this.yf() + (vec.yf() - this.yf()) * part, this.zf() + (vec.zf() - this.zf()) * part);
	}

	public Vec3F lerpF(Vec3F vec, float part) {
		return new Vec3F(this.xf() + (vec.xf() - this.xf()) * part, this.yf() + (vec.yf() - this.yf()) * part, this.zf() + (vec.zf() - this.zf()) * part);
	}

	public Vec3F lerpF(Vec3I vec, float part) {
		return new Vec3F(this.xf() + (vec.xf() - this.xf()) * part, this.yf() + (vec.yf() - this.yf()) * part, this.zf() + (vec.zf() - this.zf()) * part);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof Vec3 vec && switch (vec) {
			case Vec3I(int x2, int y2, int z2) -> this.xi == x2 && this.yi == y2 && this.zi == z2;

			case Vec3D(double x2, double y2, double z2) ->
					(double) this.xi == x2 && (double) this.yi == y2 && (double) this.zi == z2;
			case Vec3F(float x2, float y2, float z2) ->
					(double) this.xi == (double) x2 && (double) this.yi == (double) y2 && (double) this.zi == (double) z2;
			case Direction dir ->
					(double) this.xi == dir.x() && (double) this.yi == dir.y() && (double) this.zi == dir.z();
		};
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3I getAsIntVec() {
		return this;
	}

	@Override
	public String getAsArrayString() {
		return "[" + this.xi() + "," + this.yi() + "," + this.zi() + "]";
	}

	@Override
	public int compareTo(Vec3 o) {
		return Vec3.compare(this, o);
	}

	static final class Codec extends AbstractCodec<Vec3> {

		public Codec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public String valueAsKeyString(Vec3 val) {
			if (val == null) {
				return null;
			}
			return val.getAsArrayString();
		}

		@Override
		public Vec3 stringKeyToValue(String key) throws IOException {
			return stringKeyToCompositeValue(key);
		}

		@Override
		public void write(Vec3 value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			writer.writeLong(value.xi());
			writer.writeLong(value.yi());
			writer.writeLong(value.zi());
			writer.endList();
		}

		@Override
		public Vec3I read(UniversalReader reader) throws IOException {
			int x = 0;
			int y = 0;
			int z = 0;

			return switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					yield null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readInt();
					y = reader.readInt();
					z = reader.readInt();
					reader.endList();
					yield new Vec3I(x, y, z);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x", "r" -> x = reader.readInt();
							case "y", "g" -> y = reader.readInt();
							case "z", "b" -> z = reader.readInt();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec3I(x, y, z);
				}
				case INT -> new Vec3I(reader.readInt());
				case FLOAT -> new Vec3I((int) reader.readFloat());
				case LONG -> new Vec3I((int) reader.readLong());
				case DOUBLE -> new Vec3I((int) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}

}
