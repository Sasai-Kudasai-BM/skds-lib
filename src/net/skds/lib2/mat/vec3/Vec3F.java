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
@DefaultCodec(Vec3F.Codec.class)
public record Vec3F(float xf, float yf, float zf) implements Vec3 {

	public static final Vec3F XN = new Vec3F(-1.0F, 0.0F, 0.0F);
	public static final Vec3F XP = new Vec3F(1.0F, 0.0F, 0.0F);
	public static final Vec3F YN = new Vec3F(0.0F, -1.0F, 0.0F);
	public static final Vec3F YP = new Vec3F(0.0F, 1.0F, 0.0F);
	public static final Vec3F ZN = new Vec3F(0.0F, 0.0F, -1.0F);
	public static final Vec3F ZP = new Vec3F(0.0F, 0.0F, 1.0F);
	public static final Vec3F SINGLE = new Vec3F(1.0F, 1.0F, 1.0F);
	public static final Vec3F ZERO = new Vec3F(0.0F, 0.0F, 0.0F);

	@Override
	public double x() {
		return xf;
	}

	@Override
	public double y() {
		return yf;
	}

	@Override
	public double z() {
		return zf;
	}

	@Override
	public int xi() {
		return (int) xf;
	}

	@Override
	public int yi() {
		return (int) yf;
	}

	@Override
	public int zi() {
		return (int) zf;
	}

	public Vec3F(Vec3 vec) {
		this(vec.xf(), vec.yf(), vec.zf());
	}

	public Vec3F(float size) {
		this(size, size, size);
	}

	@Override
	public Vec3F up() {
		return addF(0, 1, 0);
	}

	@Override
	public Vec3F down() {
		return addF(0, -1, 0);
	}

	@Override
	public Vec3F left() {
		return addF(1, 0, 0);
	}

	@Override
	public Vec3F right() {
		return addF(-1, 0, 0);
	}

	@Override
	public Vec3F forward() {
		return addF(0, 0, 1);
	}

	@Override
	public Vec3F backward() {
		return addF(0, 0, -1);
	}

	@Override
	public Vec3F up(int i) {
		return addF(0, i, 0);
	}

	@Override
	public Vec3F down(int i) {
		return addF(0, -i, 0);
	}

	@Override
	public Vec3F left(int i) {
		return addF(i, 0, 0);
	}

	@Override
	public Vec3F right(int i) {
		return addF(-i, 0, 0);
	}

	@Override
	public Vec3F forward(int i) {
		return addF(0, 0, i);
	}

	@Override
	public Vec3F backward(int i) {
		return addF(0, 0, -i);
	}

	@Override
	public Vec3F up(double i) {
		return addF(0, (float) i, 0);
	}

	@Override
	public Vec3F down(double i) {
		return addF(0, (float) -i, 0);
	}

	@Override
	public Vec3F left(double i) {
		return addF((float) i, 0, 0);
	}

	@Override
	public Vec3F right(double i) {
		return addF((float) -i, 0, 0);
	}

	@Override
	public Vec3F forward(double i) {
		return addF(0, 0, (float) i);
	}

	@Override
	public Vec3F backward(double i) {
		return addF(0, 0, (float) -i);
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
		double x = x();
		double y = y();
		double z = z();
		double k = FastMath.invSqrt(x * x + y * y + z * z + Double.MIN_NORMAL);
		return new Vec3D(x * k, y * k, z * k);
	}

	@Override
	public Direction normalizeI() {
		return Direction.getFacing(xi(), yi(), zi());
	}

	@Override
	public Vec3F normalizeF() {
		float x = xf;
		float y = yf;
		float z = zf;
		float k = FastMath.invSqrt(x * x + y * y + z * z + Float.MIN_NORMAL);
		return new Vec3F(x * k, y * k, z * k);
	}

	@Override
	public Vec3D normalizeScale(double scale) {
		if (scale == 0) return Vec3D.ZERO;
		double x = x();
		double y = y();
		double z = z();
		double k = FastMath.invSqrt(x * x + y * y + z * z + Double.MIN_NORMAL) * scale;
		return new Vec3D(x * k, y * k, z * k);
	}

	@Override
	public Vec3I normalizeScaleI(int scale) {
		if (scale == 0) return Vec3I.ZERO;
		return Direction.getFacing(xi(), yi(), zi()).scaleI(scale);
	}

	@Override
	public Vec3F normalizeScaleF(float scale) {
		if (scale == 0) return Vec3F.ZERO;
		float x = xf;
		float y = yf;
		float z = zf;
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
			case Vec3F(float x2, float y2, float z2) -> this.xf == x2 && this.yf == y2 && this.zf == z2;

			case Vec3D(double x2, double y2, double z2) ->
					(double) this.xf == x2 && (double) this.yf == y2 && (double) this.zf == z2;
			case Vec3I(int x2, int y2, int z2) ->
					(double) this.xf == (double) x2 && (double) this.yf == (double) y2 && (double) this.zf == (double) z2;
			case Direction dir ->
					(double) this.xf == dir.x() && (double) this.yf == dir.y() && (double) this.zf == dir.z();
		};
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3F getAsFloatVec() {
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
			return "[" + val.xf() + "," + val.yf() + "," + val.zf() + "]";
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
			writer.writeFloat(value.xf());
			writer.writeFloat(value.yf());
			writer.writeFloat(value.zf());
			writer.endList();
		}

		@Override
		public Vec3F read(UniversalReader reader) throws IOException {
			float x = 0;
			float y = 0;
			float z = 0;

			return switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					yield null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readFloat();
					y = reader.readFloat();
					z = reader.readFloat();
					reader.endList();
					yield new Vec3F(x, y, z);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x", "r" -> x = reader.readFloat();
							case "y", "g" -> y = reader.readFloat();
							case "z", "b" -> z = reader.readFloat();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec3F(x, y, z);
				}
				case INT -> new Vec3F(reader.readInt());
				case FLOAT -> new Vec3F(reader.readFloat());
				case LONG -> new Vec3F(reader.readLong());
				case DOUBLE -> new Vec3F((float) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}
