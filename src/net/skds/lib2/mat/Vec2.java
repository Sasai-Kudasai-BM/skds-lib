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

@SuppressWarnings("unused")
@DefaultJsonCodec(Vec2.JCodec.class)
public sealed interface Vec2 extends Vector permits Vec2D, Vec2F, Vec2I {

	Vec2 ZERO = Vec2D.ZERO;

	@Override
	default int dimension() {
		return 2;
	}

	double x();

	double y();

	default float xf() {
		return (float) x();
	}

	default float yf() {
		return (float) y();
	}

	default int xi() {
		return (int) x();
	}

	default int yi() {
		return (int) y();
	}

	default int floorX() {
		return FastMath.floor(x());
	}

	default int floorY() {
		return FastMath.floor(y());
	}

	default int ceilX() {
		return FastMath.ceil(x());
	}

	default int ceilY() {
		return FastMath.ceil(y());
	}

	default int roundX() {
		return FastMath.round(x());
	}

	default int roundY() {
		return FastMath.round(y());
	}

	@Override
	default double get(int i) {
		return switch (i) {
			case 0 -> x();
			case 1 -> y();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int getI(int i) {
		return switch (i) {
			case 0 -> xi();
			case 1 -> yi();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default float getF(int i) {
		return switch (i) {
			case 0 -> xf();
			case 1 -> yf();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int floor(int i) {
		return switch (i) {
			case 0 -> floorX();
			case 1 -> floorY();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int ceil(int i) {
		return switch (i) {
			case 0 -> ceilX();
			case 1 -> ceilY();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	@Override
	default int round(int i) {
		return switch (i) {
			case 0 -> roundX();
			case 1 -> roundY();
			default -> throw new ArrayIndexOutOfBoundsException(i);
		};
	}

	static Vec2 of(double x, double y) {
		return new Vec2D(x, y);
	}

	static Vec2 of(float x, float y) {
		return new Vec2F(x, y);
	}

	static Vec2 of(int x, int y) {
		return new Vec2I(x, y);
	}

	static Vec2D randomNormal(Random r) {
		return normalized(r.nextFloat() - .5, r.nextFloat() - .5);
	}

	static Vec2D normalized(double x, double y) {
		double x2 = x * x;
		double y2 = y * y;
		double k = FastMath.invSqrt(x2 + y2);
		return new Vec2D(x * k, y * k);
	}

	static Vec2I normalizedI(int x, double y) {
		if (x == y) {
			return Vec2I.ZERO;
		}
		if (Math.abs(x) > Math.abs(y)) {
			if (x < 0) {
				return Vec2I.XP;
			} else {
				return Vec2I.XN;
			}
		} else {
			if (y < 0) {
				return Vec2I.YP;
			} else {
				return Vec2I.YN;
			}
		}
	}

	static Vec2F randomNormalF(Random r) {
		return normalizedF(r.nextFloat() - .5f, r.nextFloat() - .5f);
	}

	static Vec2F normalizedF(float x, float y) {
		float x2 = x * x;
		float y2 = y * y;
		float k = FastMath.invSqrt(x2 + y2);
		return new Vec2F(x * k, y * k);
	}

	default Vec2D normalize() {
		double x = x();
		double y = y();
		double d0 = x * x + y * y;
		d0 = FastMath.invSqrt(d0);
		return new Vec2D(x * d0, y * d0);
	}

	default Vec2I normalizeI() {
		return normalizedI(this.xi(), this.yi());
	}

	default Vec2F normalizeF() {
		float x = xf();
		float y = yf();
		float d0 = x * x + y * y;
		d0 = FastMath.invSqrt(d0);
		return new Vec2F(x * d0, y * d0);
	}

	default Vec2D normalizeScale(double scale) {
		double x = x();
		double y = y();
		double d0 = x * x + y * y;
		d0 = FastMath.invSqrt(d0) * scale;
		return new Vec2D(x * d0, y * d0);
	}

	default Vec2I normalizeScaleI(int scale) {
		int x = xi();
		int y = yi();
		if (x == y) {
			return Vec2I.ZERO;
		}
		if (Math.abs(x) > Math.abs(y)) {
			if (x < 0) {
				return new Vec2I(scale, 0);
			} else {
				return new Vec2I(-scale, 0);
			}
		} else {
			if (y < 0) {
				return new Vec2I(0, scale);
			} else {
				return new Vec2I(0, -scale);
			}
		}
	}

	default Vec2F normalizeScaleF(float scale) {
		float x = xf();
		float y = yf();
		float d0 = x * x + y * y;
		d0 = FastMath.invSqrt(d0) * scale;
		return new Vec2F(x * d0, y * d0);
	}

	static Vec2D randomizeGaussian(Random r, double fraction) {
		double x = r.nextGaussian() * fraction;
		double y = r.nextGaussian() * fraction;
		return new Vec2D(x, y);
	}

	static Vec2F randomizeGaussianF(Random r, float fraction) {
		float x = (float) (r.nextGaussian() * fraction);
		float y = (float) (r.nextGaussian() * fraction);
		return new Vec2F(x, y);
	}

	default double length() {
		return Math.sqrt(this.x() * this.x() + this.y() * this.y());
	}

	default double lengthSquared() {
		return this.x() * this.x() + this.y() * this.y();
	}

	static double length(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

	static double lengthSquared(double x, double y) {
		return x * x + y * y;
	}

	default int lengthI() {
		return (int) Math.sqrt(this.xi() * this.xi() + this.yi() * this.yi());
	}

	default int lengthSquaredI() {
		return this.xi() * this.xi() + this.yi() * this.yi();
	}

	static int lengthI(int x, int y) {
		return (int) Math.sqrt(x * x + y * y);
	}

	static int lengthSquaredI(int x, int y) {
		return x * x + y * y;
	}

	static float lengthF(float x, float y) {
		return (float) Math.sqrt(x * x + y * y);
	}

	static float lengthSquaredF(float x, float y) {
		return x * x + y * y;
	}

	default float lengthF() {
		return (float) Math.sqrt(this.xf() * this.xf() + this.yf() * this.yf());
	}

	default float lengthSquaredF() {
		return this.xf() * this.xf() + this.yf() * this.yf();
	}

	default Vec2D inverse() {
		return new Vec2D(-x(), -y());
	}

	default Vec2I inverseI() {
		return new Vec2I(-xi(), -yi());
	}

	default Vec2F inverseF() {
		return new Vec2F(-xf(), -yf());
	}

	default double distanceTo(Vec2 vec) {
		double dx = vec.x() - x();
		double dy = vec.y() - y();
		return Math.sqrt(dx * dx + dy * dy);
	}

	default double distanceTo(double x2, double y2) {
		x2 -= x();
		y2 -= y();
		return Math.sqrt(x2 * x2 + y2 * y2);
	}

	default double squareDistanceTo(Vec2 vec) {
		double dx = vec.x() - x();
		double dy = vec.y() - y();
		return dx * dx + dy * dy;
	}

	default double squareDistanceTo(double dx, double dy) {
		dx -= x();
		dy -= y();
		return dx * dx + dy * dy;
	}

	default int distanceToI(Vec2 vec) {
		int dx = vec.xi() - xi();
		int dy = vec.yi() - yi();
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	default int distanceToI(int x2, int y2) {
		x2 -= xi();
		y2 -= yi();
		return (int) Math.sqrt(x2 * x2 + y2 * y2);
	}

	default int squareDistanceToI(Vec2 vec) {
		int dx = vec.xi() - xi();
		int dy = vec.yi() - yi();
		return dx * dx + dy * dy;
	}

	default int squareDistanceToI(int dx, int dy) {
		dx -= xi();
		dy -= yi();
		return dx * dx + dy * dy;
	}

	default float distanceToF(Vec2 vec) {
		float dx = vec.xf() - xf();
		float dy = vec.yf() - yf();
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	default float distanceToF(float x2, float y2) {
		x2 -= xf();
		y2 -= yf();
		return (float) Math.sqrt(x2 * x2 + y2 * y2);
	}

	default float squareDistanceToF(Vec2 vec) {
		float dx = vec.xf() - xf();
		float dy = vec.yf() - yf();
		return dx * dx + dy * dy;
	}

	default float squareDistanceToF(float x2, float y2) {
		x2 -= xf();
		y2 -= yf();
		return x2 * x2 + y2 * y2;
	}

	default double dot(Vec2 vec) {
		return this.x() * vec.x() + this.y() * vec.y();
	}

	default double dot(double x2, double y2) {
		return this.x() * x2 + this.y() * y2;
	}

	static double dot(double x1, double y1, double x2, double y2) {
		return x1 * x2 + y1 * y2;
	}

	default int dotI(Vec2 vec) {
		return this.xi() * vec.xi() + this.yi() * vec.yi();
	}

	default int dotI(int x2, int y2) {
		return this.xi() * x2 + this.yi() * y2;
	}

	static int dotI(int x1, int y1, int x2, int y2) {
		return x1 * x2 + y1 * y2;
	}

	default float dotF(Vec2 vec) {
		return this.xf() * vec.xf() + this.yf() * vec.yf();
	}

	default float dotF(float x2, float y2) {
		return this.xf() * x2 + this.yf() * y2;
	}

	static float dotF(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	default Vec2D add(Vec2 vec) {
		return new Vec2D(
				this.x() + vec.x(),
				this.y() + vec.y()
		);
	}

	default Vec2D add(double x2, double y2) {
		return new Vec2D(
				this.x() + x2,
				this.y() + y2
		);
	}

	default Vec2I addI(Vec2 vec) {
		return new Vec2I(
				this.xi() + vec.xi(),
				this.yi() + vec.yi()
		);
	}

	default Vec2I addI(int x2, int y2) {
		return new Vec2I(
				this.xi() + x2,
				this.yi() + y2
		);
	}

	default Vec2F addF(Vec2 vec) {
		return new Vec2F(
				this.xf() + vec.xf(),
				this.yf() + vec.yf()
		);
	}

	default Vec2F addF(float x2, float y2) {
		return new Vec2F(
				this.xf() + x2,
				this.yf() + y2
		);
	}

	default Vec2D scale(Vec2 scale) {
		return new Vec2D(
				this.x() * scale.x(),
				this.y() * scale.y()
		);
	}

	default Vec2D scale(double sx, double sy) {
		return new Vec2D(
				this.x() * sx,
				this.y() * sy
		);
	}

	default Vec2D scale(double scale) {
		return new Vec2D(
				this.x() * scale,
				this.y() * scale
		);
	}

	default Vec2I scaleI(Vec2 scale) {
		return new Vec2I(
				this.xi() * scale.xi(),
				this.yi() * scale.yi()
		);
	}

	default Vec2I scaleI(int sx, int sy) {
		return new Vec2I(
				this.xi() * sx,
				this.yi() * sy
		);
	}

	default Vec2I scaleI(int scale) {
		return new Vec2I(
				this.xi() * scale,
				this.yi() * scale
		);
	}

	default Vec2F scaleF(Vec2 scale) {
		return new Vec2F(
				this.xf() * scale.xf(),
				this.yf() * scale.yf()
		);
	}

	default Vec2F scaleF(float sx, float sy) {
		return new Vec2F(
				this.xf() * sx,
				this.yf() * sy
		);
	}

	default Vec2F scaleF(float scale) {
		return new Vec2F(
				this.xf() * scale,
				this.yf() * scale
		);
	}

	default Vec2D div(Vec2 div) {
		return new Vec2D(
				this.x() / div.x(),
				this.y() / div.y()
		);
	}

	default Vec2D div(double sx, double sy) {
		return new Vec2D(
				this.x() / sx,
				this.y() / sy
		);
	}

	default Vec2D div(double div) {
		return new Vec2D(
				this.x() / div,
				this.y() / div
		);
	}

	default Vec2I divI(Vec2 div) {
		return new Vec2I(
				this.xi() / div.xi(),
				this.yi() / div.yi()
		);
	}

	default Vec2I divI(int sx, int sy) {
		return new Vec2I(
				this.xi() / sx,
				this.yi() / sy
		);
	}

	default Vec2I divI(int div) {
		return new Vec2I(
				this.xi() / div,
				this.yi() / div
		);
	}

	default Vec2F divF(Vec2 div) {
		return new Vec2F(
				this.xf() / div.xf(),
				this.yf() / div.yf()
		);
	}

	default Vec2F divF(float sx, float sy) {
		return new Vec2F(
				this.xf() / sx,
				this.yf() / sy
		);
	}

	default Vec2F divF(float div) {
		return new Vec2F(
				this.xf() / div,
				this.yf() / div
		);
	}

	static Vec2D add(double x1, double y1, double x2, double y2) {
		return new Vec2D(x1 + x2, y1 + y2);
	}

	static Vec2I add(int x1, int y1, int x2, int y2) {
		return new Vec2I(x1 + x2, y1 + y2);
	}

	static Vec2F addF(float x1, float y1, float x2, float y2) {
		return new Vec2F(x1 + x2, y1 + y2);
	}

	default Vec2D addScale(Vec2 vec, double scale) {
		return new Vec2D(
				this.x() + vec.x() * scale,
				this.y() + vec.y() * scale
		);
	}

	default Vec2D addScale(double x2, double y2, double scale) {
		return new Vec2D(
				this.x() + x2 * scale,
				this.y() + y2 * scale
		);
	}

	default Vec2I addScaleI(Vec2 vec, int scale) {
		return new Vec2I(
				this.xi() + vec.xi() * scale,
				this.yi() + vec.yi() * scale
		);
	}

	default Vec2I addScaleI(int x2, int y2, int scale) {
		return new Vec2I(
				this.xi() + x2 * scale,
				this.yi() + y2 * scale
		);
	}

	default Vec2F addScaleF(Vec2 vec, float scale) {
		return new Vec2F(
				this.xf() + vec.xf() * scale,
				this.yf() + vec.yf() * scale
		);
	}

	default Vec2F addScaleF(float x2, float y2, float scale) {
		return new Vec2F(
				this.xf() + x2 * scale,
				this.yf() + y2 * scale
		);
	}


	default Vec2D sub(Vec2 vec) {
		return new Vec2D(
				this.x() - vec.x(),
				this.y() - vec.y()
		);
	}

	default Vec2D invSub(Vec2 vec) {
		return new Vec2D(
				-this.x() - vec.x(),
				-this.y() - vec.y()
		);
	}

	default Vec2D sub(double x2, double y2) {
		return new Vec2D(
				this.x() - x2,
				this.y() - y2
		);
	}

	default Vec2I subI(Vec2 vec) {
		return new Vec2I(
				this.xi() - vec.xi(),
				this.yi() - vec.yi()
		);
	}

	default Vec2I invSubI(Vec2 vec) {
		return new Vec2I(
				-this.xi() - vec.xi(),
				-this.yi() - vec.yi()
		);
	}

	default Vec2I subI(int x2, int y2) {
		return new Vec2I(
				this.xi() - x2,
				this.yi() - y2
		);
	}

	default Vec2F subF(Vec2 vec) {
		return new Vec2F(
				this.xf() - vec.xf(),
				this.yf() - vec.yf()
		);
	}

	default Vec2F invSubF(Vec2 vec) {
		return new Vec2F(
				-this.xf() - vec.xf(),
				-this.yf() - vec.yf()
		);
	}

	default Vec2F subF(float x2, float y2) {
		return new Vec2F(
				this.xf() - x2,
				this.yf() - y2
		);
	}

	static Vec2D sub(double x1, double y1, double x2, double y2) {
		return new Vec2D(x1 - x2, y1 - y2);
	}

	static Vec2I subI(int x1, int y1, int x2, int y2) {
		return new Vec2I(x1 - x2, y1 - y2);
	}

	static Vec2F subF(float x1, float y1, float x2, float y2) {
		return new Vec2F(x1 - x2, y1 - y2);
	}

	default Vec2D lerp(Vec2 to, double part) {
		final double x = this.x() + (to.x() - this.x()) * part;
		final double y = this.y() + (to.y() - this.y()) * part;
		return new Vec2D(x, y);
	}

	default Vec2D lerp(double toX, double toY, double part) {
		final double x = this.x() + (toX - this.x()) * part;
		final double y = this.y() + (toY - this.y()) * part;
		return new Vec2D(x, y);
	}

	default Vec2F lerpF(Vec2 to, float part) {
		final float x = this.xf() + (to.xf() - this.xf()) * part;
		final float y = this.yf() + (to.yf() - this.yf()) * part;
		return new Vec2F(x, y);
	}

	default Vec2F lerpF(float toX, float toY, float part) {
		final float x = this.xf() + (toX - this.xf()) * part;
		final float y = this.yf() + (toY - this.yf()) * part;
		return new Vec2F(x, y);
	}

	default Vec2D clamp(double min, double max) {
		double x = this.x();
		double y = this.y();
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
		return new Vec2D(x, y);
	}

	default Vec2I clampI(int min, int max) {
		int x = this.xi();
		int y = this.yi();
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
		return new Vec2I(x, y);
	}

	default Vec2F clampF(float min, float max) {
		float x = this.xf();
		float y = this.yf();
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
		return new Vec2F(x, y);
	}


	default Vec2D flip() {
		return new Vec2D(
				1d / this.x(),
				1d / this.y()
		);
	}

	default Vec2F flipF() {
		return new Vec2F(
				1f / this.xf(),
				1f / this.yf()
		);
	}

	default Vec2D abs() {
		double x = this.x();
		double y = this.y();
		if (x < 0) {
			x = -x;
		}
		if (y < 0) {
			y = -y;
		}
		return new Vec2D(x, y);
	}

	default Vec2I absI() {
		int x = this.xi();
		int y = this.yi();
		if (x < 0) {
			x = -x;
		}
		if (y < 0) {
			y = -y;
		}
		return new Vec2I(x, y);
	}

	default Vec2F absF() {
		float x = this.xf();
		float y = this.yf();
		if (x < 0) {
			x = -x;
		}
		if (y < 0) {
			y = -y;
		}
		return new Vec2F(x, y);
	}

	default Vec2D greater(double d) {
		double x = this.x();
		double y = this.y();
		if (x < d) {
			x = d;
		}
		if (y < d) {
			y = d;
		}
		return new Vec2D(x, y);
	}

	default Vec2I greater(int d) {
		int x = this.xi();
		int y = this.yi();
		if (x < d) {
			x = d;
		}
		if (y < d) {
			y = d;
		}
		return new Vec2I(x, y);
	}

	default Vec2F greaterF(float d) {
		float x = this.xf();
		float y = this.yf();
		if (x < d) {
			x = d;
		}
		if (y < d) {
			y = d;
		}
		return new Vec2F(x, y);
	}

	default Vec2D less(double d) {
		double x = this.x();
		double y = this.y();
		if (x > d) {
			x = d;
		}
		if (y > d) {
			y = d;
		}
		return new Vec2D(x, y);
	}

	default Vec2I lessI(int d) {
		int x = this.xi();
		int y = this.yi();
		if (x > d) {
			x = d;
		}
		if (y > d) {
			y = d;
		}
		return new Vec2I(x, y);
	}

	default Vec2F lessF(float d) {
		float x = this.xf();
		float y = this.yf();
		if (x > d) {
			x = d;
		}
		if (y > d) {
			y = d;
		}
		return new Vec2F(x, y);
	}

	default Vec2D aprZeroComp(double lim) {
		double x = this.x();
		double y = this.y();
		if (Math.abs(x) < lim) {
			x = 0;
		}
		if (Math.abs(y) < lim) {
			y = 0;
		}

		if (x == 0 && y == 0) return Vec2D.ZERO;
		return new Vec2D(x, y);
	}

	default Vec2F aprZeroCompF(double lim) {
		float x = this.xf();
		float y = this.yf();
		if (Math.abs(x) < lim) {
			x = 0;
		}
		if (Math.abs(y) < lim) {
			y = 0;
		}
		if (x == 0 && y == 0) return Vec2F.ZERO;
		return new Vec2F(x, y);
	}

	default Vec2 aprZeroAll(double lim) {
		double x = this.x();
		double y = this.y();
		if (Math.abs(x) < lim && Math.abs(y) < lim) {
			return Vec2D.ZERO;
		}
		return this;
	}

	static Vec2D avg(Vec2... vectors) {
		int c = 0;

		double x = 0;
		double y = 0;
		for (Vec2 vec : vectors) {
			if (vec != null) {
				c++;
				x += vec.x();
				y += vec.y();
			}
		}

		if (c == 0) {
			return Vec2D.ZERO;
		}

		return new Vec2D(x / c, y / c);
	}

	static boolean equals(Vec2 v1, Vec2 v2) {
		if (v1 == v2) {
			return true;
		} else if ((v1 == null) != (v2 == null)) {
			return false;
		} else {
			if (v1.x() != v2.x()) {
				return false;
			} else {
				return v1.y() == v2.y();
			}
		}
	}

	static int hashCode(Vec2 vec) {
		int i = Double.hashCode(vec.x());
		return 31 * i + Double.hashCode(vec.y());
	}

	default double[] asArray() {
		return new double[]{x(), y()};
	}

	default float[] asArrayF() {
		return new float[]{xf(), yf()};
	}

	default Vec2D xy() {
		return new Vec2D(x(), y());
	}

	default Vec2F xyF() {
		return new Vec2F(xf(), yf());
	}

	default Vec2D yx() {
		return new Vec2D(y(), x());
	}

	default Vec2F yxF() {
		return new Vec2F(yf(), xf());
	}

	@Override
	default Vec2I getAsIntVec() {
		return new Vec2I(this.xi(), this.yi());
	}

	@Override
	default Vec2F getAsFloatVec() {
		return new Vec2F(this.xf(), this.yf());
	}

	@Override
	default Vec2D getAsDoubleVec() {
		return new Vec2D(this.x(), this.y());
	}

	static final class JCodec extends AbstractJsonCodec<Vec2> {

		private final JsonSerializer<Vec2I> veci = this.registry.getSerializerIndirect(Vec2I.class);
		private final JsonSerializer<Vec2> vecd = this.registry.getSerializerIndirect(Vec2D.class);

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Vec2 value, JsonWriter writer) throws IOException {
			if (value instanceof Vec2I vec) {
				this.veci.write(vec, writer);
			} else {
				this.vecd.write(value, writer);
			}
		}

		@Override
		public Vec2 read(JsonReader reader) throws IOException {
			Number x = 0;
			Number y = 0;
			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					x = reader.readNumber();
					y = reader.readNumber();
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
						}
					}
					reader.endObject();
				}
				case NUMBER -> {
					Number value = reader.readNumber();
					x = value;
					y = value;
				}
				default ->
						throw new JsonReadException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}
			if (x instanceof Double || y instanceof Double) {
				return new Vec2D(x.doubleValue(), y.doubleValue());
			} else {
				return new Vec2I(x.intValue(), y.intValue());
			}
		}
	}
}
