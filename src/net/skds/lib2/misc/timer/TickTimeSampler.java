package net.skds.lib2.misc.timer;

public class TickTimeSampler {

	private final long[] samples;
	private int pos = 0;
	private int size = 0;

	public TickTimeSampler(int samples) {
		this.samples = new long[samples];
	}

	public void putNanos(long nanos) {
		int p = pos + 1;
		if (size < p) size = p;
		p %= samples.length;
		pos = p;
		samples[p] = nanos;
	}

	public void putMillis(double millis) {
		putNanos((long) (millis * 1_000_000));
	}

	public double getAvgMillis() {
		long t = 0;
		int l = size;
		for (int i = 0; i < l; i++) {
			t += samples[i];
		}
		return 1E-6D * t / l;
	}

	public long getAvgNanos() {
		long t = 0;
		int l = size;
		for (int i = 0; i < l; i++) {
			t += samples[i];
		}
		return t / l;
	}

	public double getLastMillis() {
		return 1E-6D * samples[pos];
	}

	public long getLastNanos() {
		return samples[pos];
	}
}
