package net.skds.lib2.misc.noise;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.misc.random.StateFuncRandom;

public class Noise {

	//private static final int HASH = "jop00a".hashCode();

	private final float[] amplitudes;
	private final StateFuncRandom[] layers;
	private final int layerCount;

	public Noise(long seed, float[] amplitudes) {
		int layerCount = amplitudes.length;
		this.layerCount = layerCount;
		this.amplitudes = amplitudes;
		this.layers = new StateFuncRandom[layerCount];

		StateFuncRandom layer0 = new StateFuncRandom(seed);
		layers[0] = layer0;
		for (int i = 1; i < layerCount; i++) {
			layers[i] = new StateFuncRandom((seed ^ i) + 37);
		}
	}

	public float getValueInPoint(double x, double y) {
		float value = 0;
		for (int i = layerCount - 1; i >= 0; i--) {
			float amp0 = amplitudes[i];
			if (amp0 == 0) {
				continue;
			}
			int n = i + 1;
			float amp = amp0 / n;

			double xi = x * n;
			double yi = y * n;

			int x0 = FastMath.floor(xi);
			int x1 = FastMath.ceil(xi);
			float kx = (float) (xi - x0);
			int y0 = FastMath.floor(yi);
			int y1 = FastMath.ceil(yi);
			float ky = (float) (yi - y0);

			StateFuncRandom sfr = layers[i];

			float v00 = sfr.randomize(x0, y0);
			float v10 = sfr.randomize(x1, y0);
			float v01 = sfr.randomize(x0, y1);
			float v11 = sfr.randomize(x1, y1);

			float s0 = FastMath.lerp(ky, v00, v01);
			float s1 = FastMath.lerp(ky, v10, v11);

			value += FastMath.lerp(kx, s0, s1) * amp;
		}

		return value;
	}

	public float getValueInPoint(double x, double y, double z) {
		float value = 0;
		for (int i = layerCount - 1; i >= 0; i--) {
			float amp0 = amplitudes[i];
			if (amp0 == 0) {
				continue;
			}
			int n = i + 1;
			float amp = amp0 / n;

			double xi = x * n;
			double yi = y * n;
			double zi = z * n;

			int x0 = FastMath.floor(xi);
			int x1 = FastMath.ceil(xi);
			float kx = (float) (xi - x0);
			int y0 = FastMath.floor(yi);
			int y1 = FastMath.ceil(yi);
			float ky = (float) (yi - y0);
			int z0 = FastMath.floor(zi);
			int z1 = FastMath.ceil(zi);
			float kz = (float) (zi - z0);

			StateFuncRandom sfr = layers[i];

			float v000 = sfr.randomize(x0, y0, z0);
			float v100 = sfr.randomize(x1, y0, z0);
			float v010 = sfr.randomize(x0, y1, z0);
			float v110 = sfr.randomize(x1, y1, z0);
			float v001 = sfr.randomize(x0, y0, z1);
			float v101 = sfr.randomize(x1, y0, z1);
			float v011 = sfr.randomize(x0, y1, z1);
			float v111 = sfr.randomize(x1, y1, z1);

			float b00 = FastMath.lerp(kz, v000, v001);
			float b10 = FastMath.lerp(kz, v100, v101);
			float b01 = FastMath.lerp(kz, v010, v011);
			float b11 = FastMath.lerp(kz, v110, v111);

			float s0 = FastMath.lerp(ky, b00, b01);
			float s1 = FastMath.lerp(ky, b10, b11);

			value += FastMath.lerp(kx, s0, s1) * amp;
		}

		return value;
	}

}
