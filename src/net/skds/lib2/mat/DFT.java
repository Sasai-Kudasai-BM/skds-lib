package net.skds.lib2.mat;

import net.skds.lib2.mat.complex.ComplexFloatArray;

public class DFT {

	private final ComplexFloatArray omega;

	private final int size;

	public DFT(int size) {
		if (!FastMath.isPowerOf2(size)) {
			throw new IllegalArgumentException("Size must be power of 2 but got " + size);
		}
		this.size = size;
		ComplexFloatArray om = new ComplexFloatArray(size);

		/*
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < N; j++) {
				final int k = i * N + j;
				tableRe[k] = FastMath.cos(360f * i * j / N);
				tableIm[k] = FastMath.sin(-360f * i * j / N);
			}
		}

		 */
		this.omega = om;
	}

	public void cum(float[] in) {
		for (int n = size; n >= 2; n >>= 1) {
			final int n2 = n >> 1;

			for (int c = 0; c < size; c++) {
				final int k = c + ((2 * c / n) * n2);

				// E(k)
				// O(k)

				// X(k) = E(k) + omega^k * O(k)
				// X(k + N/2) = E(k) - omega^k * O(k)

				/*
				e.set(encodeBuffer[k]);
				o.set(encodeBuffer[k + n2]);
				ComplexFloat r = encodeBuffer[k];
				ComplexFloat r2 = encodeBuffer[k + n2];

				r.set(e);
				r2.set(e);

				r.add(o);
				r2.sub(o);

				if (n != 2) {
					r2.mul(omega(c % n2, n));
				}

				 */

			}
		}
	}
}
