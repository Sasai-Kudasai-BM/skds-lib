package net.skds.lib.mat;

import java.util.Random;

public class FastMath {

	public static final Random RANDOM = new Random();

	public static double modInt(double a, int b) {
		int div = ((int) a) / b;
		//log.info(div);
		return a - (div * b);
	}

	public static float modInt(float a, int b) {
		int div = ((int) a) / b;
		//log.info(div);
		return a - (div * b);
	}

	public static double sinOld(double x) {
		x = aprSinDegr(x);
		double a;
		if (x > 180) {
			x = 360 - x;
			a = -1;
		} else {
			a = 1;
		}
		a *= 4 * x * (180 - x);
		double b = 40500 - (x * (180 - x));

		return a / b;
	}

	public static float sinOld(float x) {
		x = aprSinDegr(x);
		float a;
		if (x > 180) {
			x = 360 - x;
			a = -1;
		} else {
			a = 1;
		}
		a *= 4 * x * (180 - x);
		float b = 40500 - (x * (180 - x));

		return a / b;
	}

	public static double cosOld(double x) {
		return sinOld(x + 90);
	}

	public static float cosOld(float x) {
		return sinOld(x + 90);
	}

	private static double aprSinDegr(double x) {
		x = modInt(x, 360);
		if (x < 0) {
			x = 360 + x;
		}
		return x;
	}

	private static float aprSinDegr(float x) {
		x = modInt(x, 360);
		if (x < 0) {
			x += 360;
		}
		return x;
	}

	public static double wrapDegrees(double degrees) {
		double d = modInt(degrees, 360);
		if (d >= 180.0) {
			d -= 360.0;
		}
		if (d < -180.0) {
			d += 360.0;
		}
		return d;
	}

	public static float wrapDegrees(float degrees) {
		float d = modInt(degrees, 360);
		if (d >= 180.0) {
			d -= 360.0;
		}
		if (d < -180.0) {
			d += 360.0;
		}
		return d;
	}

	public static double clamp(double value, double min, double max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static float clamp(float value, float min, float max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		if (value > max) {
			return max;
		}
		return value;
	}

	public static double clampAngle(double angle, double limit) {
		double a = wrapDegrees(angle);
		a = clamp(a, -limit, limit);
		return a;
	}

	public static float clampAngle(float angle, float limit) {
		float a = wrapDegrees(angle);
		a = clamp(a, -limit, limit);
		return a;
	}

	public static double clampAngle(double start, double end, double speed) {
		double a = wrapDegrees(end - start);
		a = clamp(a, -speed, speed);
		return a;
	}

	public static float clampAngle(float start, float end, float speed) {
		float a = wrapDegrees(end - start);
		a = clamp(a, -speed, speed);
		return a;
	}

	public static double angleDiffDeg(double a1, double a2) {
		double diff = (a1 - a2) % 360;
		return diff > 180 ? diff - 360 : diff;
	}

	public static float angleDiffDeg(float a1, float a2) {
		float diff = (a1 - a2) % 360;
		if (diff > 180) {
			diff -= 360;
		} else if (diff < -180) {
			diff += 360;
		}
		return diff;
	}

	public static double lerp(double t, double min, double max) {
		return (max - min) * t + min;
	}

	public static float lerp(float t, float min, float max) {
		return (max - min) * t + min;
	}

	public static float sin(float a) {
		int b = (int) (a * sinTable.length / 360);
		b %= sinTable.length;
		if (b < 0) {
			b += sinTable.length;
		}
		return sinTable[b];
	}

	public static float cos(final float x) {
		return sin(x + 90);
	}

	private static final float[] sinTable = new float[4096 * 4];

	static {
		for (int i = 0; i < sinTable.length; i++) {
			sinTable[i] = (float) StrictMath.sin(2 * Math.PI * i / sinTable.length);
		}
	}

	public static int floor(double value) {
		int i = (int) value;
		return i <= value ? i : i - 1;
	}

	public static int ceil(double value) {
		int i = (int) value;
		return i >= value ? i : i + 1;
	}

	public static int floor(float value) {
		int i = (int) value;
		return i <= value ? i : i - 1;
	}

	public static int ceil(float value) {
		int i = (int) value;
		return i >= value ? i : i + 1;
	}

	public static int smallestEncompassingPowerOfTwo(int value) {
		int i = value - 1;
		i |= i >> 1;
		i |= i >> 2;
		i |= i >> 4;
		i |= i >> 8;
		i |= i >> 16;
		return i + 1;
	}

	public static boolean approxEqualSq(double a, double b, double eps) {
		double d = a - b;
		return d * d < eps;
	}

	public static boolean approxEqual(double a, double b, double eps) {
		return Math.abs(a - b) < eps;
	}
}
