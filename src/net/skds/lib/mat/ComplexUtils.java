package net.skds.lib.mat;

public class ComplexUtils {

	public static float complexMulRe(float aRe, float aIm, float bRe, float bIm) {
		return aRe * bRe - aIm * bIm;
	}

	public static float complexMulIm(float aRe, float aIm, float bRe, float bIm) {
		return aRe * bIm + bRe * aIm;
	}

	public static class ComplexFloat {
		public float re;
		public float im;

		public ComplexFloat() {
		}

		public ComplexFloat(float re, float im) {
			this.re = re;
			this.im = im;
		}

		public void add(ComplexFloat val) {
			this.re += val.re;
			this.im += val.im;
		}

		public void reset() {
			this.re = 0;
			this.im = 0;
		}

		public void set(float val) {
			this.re = val;
			this.im = 0;
		}

		public void set(float re, float im) {
			this.re = re;
			this.im = im;
		}

		public void set(ComplexFloat val) {
			this.re = val.re;
			this.im = val.im;
		}

		public void sub(ComplexFloat val) {
			this.re -= val.re;
			this.im -= val.im;
		}

		public void mul(ComplexFloat val) {
			final float tre = this.re * val.re - this.im * val.im;
			this.im = this.re * val.im + this.im * val.re;
			this.re = tre;
		}

		public void mulRev(ComplexFloat omega) {

			final float ore = omega.re;
			final float oim = -omega.im;

			final float tre = this.re * ore - this.im * oim;
			this.im = this.re * oim + this.im * ore;
			this.re = tre;
		}

		public void mul(float re, float im) {
			final float tre = this.re * re - this.im * im;
			this.im = this.re * im + this.im * re;
			this.re = tre;
		}

		@Override
		public String toString() {
			return "{re:%.2f im:%.2f}".formatted(re, im);
		}
	}
}
