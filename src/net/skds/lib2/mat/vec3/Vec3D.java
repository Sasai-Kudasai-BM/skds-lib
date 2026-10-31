package net.skds.lib2.mat.vec3;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.mat.FastMath;

import java.io.IOException;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
@DefaultCodec(Vec3D.Codec.class)
public record Vec3D(double x, double y, double z) implements Vec3 {

	public static final Vec3D XN = new Vec3D(-1.0D, 0.0D, 0.0D);
	public static final Vec3D XP = new Vec3D(1.0D, 0.0D, 0.0D);
	public static final Vec3D YN = new Vec3D(0.0D, -1.0D, 0.0D);
	public static final Vec3D YP = new Vec3D(0.0D, 1.0D, 0.0D);
	public static final Vec3D ZN = new Vec3D(0.0D, 0.0D, -1.0D);
	public static final Vec3D ZP = new Vec3D(0.0D, 0.0D, 1.0D);
	public static final Vec3D SINGLE = new Vec3D(1.0D, 1.0D, 1.0D);
	public static final Vec3D ZERO = new Vec3D(0.0D, 0.0D, 0.0D);

	public Vec3D(Vec3 vec) {
		this(vec.x(), vec.y(), vec.z());
	}

	public Vec3D(double size) {
		this(size, size, size);
	}

	@Override
	public Vec3D up() {
		return add(0, 1, 0);
	}

	@Override
	public Vec3D down() {
		return add(0, -1, 0);
	}

	@Override
	public Vec3D left() {
		return add(1, 0, 0);
	}

	@Override
	public Vec3D right() {
		return add(-1, 0, 0);
	}

	@Override
	public Vec3D forward() {
		return add(0, 0, 1);
	}

	@Override
	public Vec3D backward() {
		return add(0, 0, -1);
	}

	@Override
	public Vec3D up(int i) {
		return add(0, i, 0);
	}

	@Override
	public Vec3D down(int i) {
		return add(0, -i, 0);
	}

	@Override
	public Vec3D left(int i) {
		return add(i, 0, 0);
	}

	@Override
	public Vec3D right(int i) {
		return add(-i, 0, 0);
	}

	@Override
	public Vec3D forward(int i) {
		return add(0, 0, i);
	}

	@Override
	public Vec3D backward(int i) {
		return add(0, 0, -i);
	}

	@Override
	public Vec3D up(double i) {
		return add(0, i, 0);
	}

	@Override
	public Vec3D down(double i) {
		return add(0, -i, 0);
	}

	@Override
	public Vec3D left(double i) {
		return add(i, 0, 0);
	}

	@Override
	public Vec3D right(double i) {
		return add(-i, 0, 0);
	}

	@Override
	public Vec3D forward(double i) {
		return add(0, 0, i);
	}

	@Override
	public Vec3D backward(double i) {
		return add(0, 0, -i);
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
		double x = this.x;
		double y = this.y;
		double z = this.z;
		double k = FastMath.invSqrt(x * x + y * y + z * z + Double.MIN_NORMAL);
		return new Vec3D(x * k, y * k, z * k);
	}

	@Override
	public Vec3F normalizeF() {
		float x = xf();
		float y = yf();
		float z = zf();
		float k = FastMath.invSqrt(x * x + y * y + z * z + Float.MIN_NORMAL);
		return new Vec3F(x * k, y * k, z * k);
	}

	@Override
	public Vec3D normalizeScale(double scale) {
		if (scale == 0) return Vec3D.ZERO;
		double x = this.x;
		double y = this.y;
		double z = this.z;
		double k = FastMath.invSqrt(x * x + y * y + z * z + Double.MIN_NORMAL) * scale;
		return new Vec3D(x * k, y * k, z * k);
	}

	@Override
	public Vec3F normalizeScaleF(float scale) {
		if (scale == 0) return Vec3F.ZERO;
		float x = xf();
		float y = yf();
		float z = zf();
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
			case Vec3D(double x2, double y2, double z2) -> this.x == x2 && this.y == y2 && this.z == z2;
			case Vec3F(float x2, float y2, float z2) -> this.x == x2 && this.y == y2 && this.z == z2;
			case Vec3I(int x2, int y2, int z2) -> this.x == x2 && this.y == y2 && this.z == z2;
			case Direction dir -> this.x == dir.x() && this.y == dir.y() && this.z == dir.z();
		};
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3D getAsDoubleVec() {
		return this;
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
			writer.writeDouble(value.x());
			writer.writeDouble(value.y());
			writer.writeDouble(value.z());
			writer.endList();
		}

		@Override
		public Vec3D read(UniversalReader reader) throws IOException {
			double x = 0;
			double y = 0;
			double z = 0;

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
					reader.endList();
					yield new Vec3D(x, y, z);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x", "r" -> x = reader.readDouble();
							case "y", "g" -> y = reader.readDouble();
							case "z", "b" -> z = reader.readDouble();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec3D(x, y, z);
				}
				case INT -> new Vec3D(reader.readInt());
				case FLOAT -> new Vec3D(reader.readFloat());
				case LONG -> new Vec3D(reader.readLong());
				case DOUBLE -> new Vec3D(reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}
