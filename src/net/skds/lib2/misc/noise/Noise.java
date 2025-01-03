package net.skds.lib2.misc.noise;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.misc.random.StateFuncRandom;

public class Noise {


	private final float weightCorrection;
	private final float exponent;
	private final float phaseScale;
	private final float[] amplitudes;
	private final StateFuncRandom[] layers;
	private final int layerCount;
	@SuppressWarnings({"unused", "FieldCanBeLocal"})
	private final int actualLayerCount;
	private final FastMath.FloatInterpolation interpolation;

	public Noise(long seed, int layerCount, AmplitudeFunction amplitudeFunction, float exponent, FastMath.FloatInterpolation interpolation) {
		this.exponent = exponent;
		this.layerCount = layerCount;
		float[] amplitudes = new float[layerCount];
		for (int i = 0; i < layerCount; i++) {
			amplitudes[i] = amplitudeFunction.amplitude(i, exponent);
		}
		this.amplitudes = amplitudes;
		this.interpolation = interpolation;
		this.layers = new StateFuncRandom[layerCount];

		StateFuncRandom layer0 = new StateFuncRandom(seed);
		layers[0] = layer0;
		float ps = exponent;
		float weight = amplitudes[0];
		int lc = 1;
		for (int i = 1; i < layerCount; i++) {
			ps *= exponent;
			float amp = amplitudes[i];
			if (amp == 0) continue;
			lc++;
			weight += amp;
			layers[i] = new StateFuncRandom((seed ^ i) + 37);
		}
		this.weightCorrection = 1 / weight;
		this.phaseScale = 1 / ps;
		this.actualLayerCount = lc;
	}

	public float getValueInPoint(double x, double y, double z) {
		x *= phaseScale;
		y *= phaseScale;
		z *= phaseScale;
		float value = 0;
		float ps = 1;
		for (int i = 0; i < layerCount; i++) {
			ps *= exponent;
			StateFuncRandom sfr = layers[i];
			if (sfr == null) {
				continue;
			}
			float amp = amplitudes[i];

			double xi = x * ps;
			double yi = y * ps;
			double zi = z * ps;

			int x0 = FastMath.floor(xi);
			int x1 = FastMath.ceil(xi);
			float kx = (float) (xi - x0);
			int y0 = FastMath.floor(yi);
			int y1 = FastMath.ceil(yi);
			float ky = (float) (yi - y0);
			int z0 = FastMath.floor(zi);
			int z1 = FastMath.ceil(zi);
			float kz = (float) (zi - z0);

			float v000 = sfr.randomize(x0, y0, z0);
			float v100 = sfr.randomize(x1, y0, z0);
			float v010 = sfr.randomize(x0, y1, z0);
			float v110 = sfr.randomize(x1, y1, z0);
			float v001 = sfr.randomize(x0, y0, z1);
			float v101 = sfr.randomize(x1, y0, z1);
			float v011 = sfr.randomize(x0, y1, z1);
			float v111 = sfr.randomize(x1, y1, z1);

			float b00 = interpolation.interpolate(kz, v000, v001);
			float b10 = interpolation.interpolate(kz, v100, v101);
			float b01 = interpolation.interpolate(kz, v010, v011);
			float b11 = interpolation.interpolate(kz, v110, v111);

			float s0 = interpolation.interpolate(ky, b00, b01);
			float s1 = interpolation.interpolate(ky, b10, b11);

			value += interpolation.interpolate(kx, s0, s1) * amp;
		}

		return value * weightCorrection;
	}

	public float getValueInPoint(double x, double y) {
		x *= phaseScale;
		y *= phaseScale;
		float value = 0;
		float ps = 1;
		for (int i = 0; i < layerCount; i++) {
			ps *= exponent;
			StateFuncRandom sfr = layers[i];
			if (sfr == null) {
				continue;
			}
			float amp = amplitudes[i];

			double xi = x * ps;
			double yi = y * ps;

			int x0 = FastMath.floor(xi);
			int x1 = FastMath.ceil(xi);
			float kx = (float) (xi - x0);
			int y0 = FastMath.floor(yi);
			int y1 = FastMath.ceil(yi);
			float ky = (float) (yi - y0);


			float v00 = sfr.randomize(x0, y0);
			float v10 = sfr.randomize(x1, y0);
			float v01 = sfr.randomize(x0, y1);
			float v11 = sfr.randomize(x1, y1);

			float s0 = interpolation.interpolate(ky, v00, v01);
			float s1 = interpolation.interpolate(ky, v10, v11);

			value += interpolation.interpolate(kx, s0, s1) * amp;
		}

		return value * weightCorrection;
	}

	/*
	public FloatField2D[] createFieldBuffer(int width, int height) {
		FloatField2D[] fields = new FloatField2D[actualLayerCount];
		float ps = phaseScale;
		int n = 0;
		for (int i = 0; i < layerCount; i++) {
			ps *= periodScale;
			if (layers[i] == null) {
				continue;
			}
			float xi = width * ps;
			float yi = height * ps;
			int xs = FastMath.ceil(xi) + 1;
			int ys = FastMath.ceil(yi) + 1;
			fields[n++] = new FloatField2DImpl(xs, ys);
		}
		return fields;
	}

	public FloatField2D createFillField(FloatField2D[] fields, double xOffset, double yOffset) {
		float ps = 1;
		int n = 0;
		for (int i = 0; i < layerCount; i++) {
			ps *= periodScale;
			StateFuncRandom sfr = layers[i];
			if (sfr == null) {
				continue;
			}
			FloatField2D field = fields[n++];
			int w = field.width();
			int h = field.height();
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					float value = getLayerValueInPoint(i, x + xOffset, y + yOffset);
					field.setValue(value, x, y);
				}
			}

			float xi = width * ps;
			float yi = height * ps;
			int xs = FastMath.ceil(xi) + 1;
			int ys = FastMath.ceil(yi) + 1;
			 =new FloatField2DImpl(xs, ys);
		}
		return fields;
	}


	//public float getLayerValueInPoint(int layer, double x, double y) {
	//}
	 */

	public interface AmplitudeFunction {
		float amplitude(int layer, float exponent);
	}
}
