package net.w3e.lib.mat;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NoiseUtil {
	public static float octave(int octaves, INoise2 noise, float fx, float fy, float persistence) {
		float amplitude = 1;
		float max = 0;
		float result = 0;

		while (octaves-- > 0) {
			max += amplitude;
			result += noise.noise(fx, fy) * amplitude;
			amplitude *= persistence;
			fx *= 2;
			fy *= 2;
		}

		return result/max;
	}

	public interface INoise2 {
		float noise(float x, float y);
	}

	public static float octave(int octaves, INoise3 noise, float fx, float fy, float fz, float persistence) {
		float amplitude = 1;
		float max = 0;
		float result = 0;

		while (octaves-- > 0) {
			max += amplitude;
			result += noise.noise(fx, fy, fz) * amplitude;
			amplitude *= persistence;
			fx *= 2;
			fy *= 2;
			fz *= 2;
		}

		return result/max;
	}

	public interface INoise3 {
		float noise(float x, float y, float z);
	}
}
