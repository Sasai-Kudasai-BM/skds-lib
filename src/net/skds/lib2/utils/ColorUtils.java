package net.skds.lib2.utils;

import lombok.experimental.UtilityClass;
import net.skds.lib2.mat.FastMath;

@SuppressWarnings("unused")
@UtilityClass
public class ColorUtils {

	public static int hueRGBv0(float hue) {
		hue = FastMath.mod1(hue);
		int huInt = (int) (hue * 255 * 3);
		int i = huInt % 256;
		switch (huInt / 256) {
			case 0 -> {
				return packRGB(255 - i, i, 0);
			}
			case 1 -> {
				return packRGB(0, 255 - i, i);
			}
			case 2 -> {
				return packRGB(i, 0, 255 - i);
			}
		}
		return 0;
	}

	public static int hueRGB(float hue) {
		hue = FastMath.mod1(hue);
		int huInt = (int) (hue * 255 * 6);
		int i = huInt % 256;
		switch (huInt / 256) {
			case 0 -> {
				return packRGB(255, i, 0);
			}
			case 1 -> {
				return packRGB(255 - i, 255, 0);
			}
			case 2 -> {
				return packRGB(0, 255, i);
			}
			case 3 -> {
				return packRGB(0, 255 - i, 255);
			}
			case 4 -> {
				return packRGB(i, 0, 255);
			}
			case 5 -> {
				return packRGB(255, 0, 255 - i);
			}
		}
		return 0;
	}

	public static int scaleRGB(int brightness, int rgb) {
		return packRGB(brightness * unpackR(rgb) / 255, brightness * unpackG(rgb) / 255, brightness * unpackB(rgb) / 255);
	}

	public static int grayRGB(int brightness) {
		brightness &= 0xff;
		return (brightness << 8 | brightness) << 8 | brightness;
	}

	public static int packARGB(int rgb, int a) {
		return (0xff & a) << 24 | (0xffffff & rgb);
	}

	public static int packARGB(int r, int g, int b, int a) {
		return (((0xff & a) << 8 | (0xff & r)) << 8 | (0xff & g)) << 8 | (0xff & b);
	}

	public static int packRGB(int r, int g, int b) {
		return ((0xff & r) << 8 | (0xff & g)) << 8 | (0xff & b);
	}

	public static int packARGB(byte r, byte g, byte b, byte a) {
		return ((a << 8 | r) << 8 | g) << 8 | b;
	}

	public static int packRGB(byte r, byte g, byte b) {
		return (r << 8 | g) << 8 | b;
	}

	public static int packARGB(float r, float g, float b, float a) {
		return (((0xff & FastMath.floor(a * 255)) << 8 |
				(0xff & FastMath.floor(r * 255))) << 8 |
				(0xff & FastMath.floor(g * 255))) << 8 |
				(0xff & FastMath.floor(b * 255));
	}

	public static int packRGB(float r, float g, float b) {
		return ((0xff & FastMath.floor(r * 255)) << 8 |
				(0xff & FastMath.floor(g * 255))) << 8 |
				(0xff & FastMath.floor(b * 255));
	}

	public static int unpackR(int argb) {
		return argb >> 16 & 0xff;
	}

	public static int unpackG(int argb) {
		return argb >> 8 & 0xff;
	}

	public static int unpackB(int argb) {
		return argb & 0xff;
	}

	public static int unpackA(int argb) {
		return argb >> 24;
	}

}
