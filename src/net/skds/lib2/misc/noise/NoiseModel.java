package net.skds.lib2.misc.noise;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.misc.fields.FloatField2D;
import net.skds.lib2.misc.fields.FloatField2DImpl;
import net.skds.lib2.misc.random.StaticRandom;

import java.util.Arrays;

public class NoiseModel {

	private final float weightCorrection;
	private final float[] amplitudes;
	private final float[] scales;
	private final StaticRandom[] layers;
	private final int actualLayerCount;
	private final FastMath.FloatInterpolation interpolation;

	public NoiseModel(long seed, int layerCount, AmplitudeFunction amplitudeFunction, float exponent, FastMath.FloatInterpolation interpolation) {
		this.interpolation = interpolation;
		float[] amplitudes = new float[layerCount];
		float[] pss = new float[layerCount];
		int n = 0;
		float ps = 1;

		float weight = 0;
		for (int i = 0; i < layerCount; i++) {
			ps *= exponent;
			float amp = amplitudeFunction.amplitude(i, exponent);
			if (amp == 0) continue;
			weight += amp;
			pss[n] = ps;
			amplitudes[n++] = amp;
		}
		this.actualLayerCount = n;
		this.amplitudes = Arrays.copyOf(amplitudes, n);
		this.layers = new StaticRandom[n];
		for (int i = 0; i < n; i++) {
			pss[i] /= ps;
			layers[i] = new StaticRandom((seed ^ i) + 37);
		}
		this.scales = Arrays.copyOf(pss, n);
		this.weightCorrection = 1 / weight;
		//this.phaseScale = 1 / ps;
	}

	public float getValueInPoint(double x, double y, double z) {
		float value = 0;
		for (int i = 0; i < actualLayerCount; i++) {
			float ps = scales[i];
			StaticRandom sfr = layers[i];

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

			float v000 = sfr.randomizeFloat(x0, y0, z0);
			float v100 = sfr.randomizeFloat(x1, y0, z0);
			float v010 = sfr.randomizeFloat(x0, y1, z0);
			float v110 = sfr.randomizeFloat(x1, y1, z0);
			float v001 = sfr.randomizeFloat(x0, y0, z1);
			float v101 = sfr.randomizeFloat(x1, y0, z1);
			float v011 = sfr.randomizeFloat(x0, y1, z1);
			float v111 = sfr.randomizeFloat(x1, y1, z1);

			float b00 = interpolation.interpolate(kz, v000, v001);
			float b10 = interpolation.interpolate(kz, v100, v101);
			float b01 = interpolation.interpolate(kz, v010, v011);
			float b11 = interpolation.interpolate(kz, v110, v111);

			float s0 = interpolation.interpolate(ky, b00, b01);
			float s1 = interpolation.interpolate(ky, b10, b11);

			value += interpolation.interpolate(kx, s0, s1) * amplitudes[i];
		}

		return value * weightCorrection;
	}

	public float getValueInPoint(double x, double y) {
		float value = 0;
		for (int i = 0; i < actualLayerCount; i++) {
			float ps = scales[i];
			StaticRandom sfr = layers[i];
			double xi = x * ps;
			double yi = y * ps;

			int x0 = FastMath.floor(xi);
			int x1 = FastMath.ceil(xi);
			float kx = (float) (xi - x0);
			int y0 = FastMath.floor(yi);
			int y1 = FastMath.ceil(yi);
			float ky = (float) (yi - y0);


			float v00 = sfr.randomizeFloat(x0, y0);
			float v10 = sfr.randomizeFloat(x1, y0);
			float v01 = sfr.randomizeFloat(x0, y1);
			float v11 = sfr.randomizeFloat(x1, y1);

			float s0 = interpolation.interpolate(ky, v00, v01);
			float s1 = interpolation.interpolate(ky, v10, v11);

			value += interpolation.interpolate(kx, s0, s1) * amplitudes[i];
		}

		return value * weightCorrection;
	}

	public Field createFieldBuffer(int width, int height, int x, int y) {
		FloatField2D[] fields = new FloatField2D[actualLayerCount];
		for (int i = 0; i < actualLayerCount; i++) {
			float ps = scales[i];
			float xi = width * ps;
			float yi = height * ps;
			int xs = FastMath.ceil(xi) + 1;
			int ys = FastMath.ceil(yi) + 1;
			fields[i] = new FloatField2DImpl(xs, ys);
		}
		return new Field(fields, scales, width, height);
	}

	/*
	public void fillFields(Field nf, double xOffset, double yOffset) {
		if (nf.fields.length != this.actualLayerCount) throw new IllegalArgumentException("Incompatible field depth");
		for (int i = 0; i < actualLayerCount; i++) {
			float ps = scales[i];
			FloatField2D field = nf.fields[i];
			StateFuncRandom sfr = layers[i];
			int w = field.width();
			int h = field.height();
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {

					int xi = FastMath.floor(x + (xOffset) / ps);
					int yi = FastMath.floor(y + (yOffset) / ps);

					float value = sfr.randomize(xi, yi) * amplitudes[i];
					field.setValue(value, x, y);
				}
			}
		}
	}


	public FloatField2D getField(Field nf) {
		if (nf.fields.length != this.actualLayerCount) throw new IllegalArgumentException("Incompatible field depth");
		FloatField2D out = new FloatField2DImpl(nf.targetWidth, nf.targetHeight);
		for (int x = 0; x < nf.targetWidth; x++) {
			for (int y = 0; y < nf.targetHeight; y++) {
				float value = 0;
				for (int i = 0; i < actualLayerCount; i++) {
					float ps = scales[i];
					FloatField2D field = nf.fields[i];

					double xi = x * ps;
					double yi = y * ps;

					int x0 = FastMath.floor(xi);
					int x1 = FastMath.ceil(xi);
					float kx = (float) (xi - x0);
					int y0 = FastMath.floor(yi);
					int y1 = FastMath.ceil(yi);
					float ky = (float) (yi - y0);

					float v00 = field.getValue(x0, y0);
					float v10 = field.getValue(x1, y0);
					float v01 = field.getValue(x0, y1);
					float v11 = field.getValue(x1, y1);

					float s0 = interpolation.interpolate(ky, v00, v01);
					float s1 = interpolation.interpolate(ky, v10, v11);

					value += interpolation.interpolate(kx, s0, s1);
				}

				out.setValue(value, x, y);
			}
		}
		return out;

	}

	 //*/
	public record Field(FloatField2D[] fields, float[] phaseScales, int targetWidth, int targetHeight) {
	}


	public interface AmplitudeFunction {
		float amplitude(int layer, float exponent);

		AmplitudeFunction EXPONENT = (l, e) -> {
			float amp = e;
			for (int i = 1; i < l; i++) {
				amp *= e;
			}
			return 1 / amp;
		};
		AmplitudeFunction SQUARE = (l, e) -> {
			float a = (l + 1) * e;
			return 1f / (a * a);
		};
		AmplitudeFunction LINEAR = (l, _) -> 1f / (l + 1);
		AmplitudeFunction FIBONACCI = (l, _) -> {
			float a = 1;
			float a0 = 1;
			for (int i = 1; i < l; i++) {
				float b = a;
				a += a0;
				a0 = b;
			}
			return 1f / a;
		};
	}
}
