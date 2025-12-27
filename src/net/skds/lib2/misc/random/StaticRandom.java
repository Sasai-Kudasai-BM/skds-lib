package net.skds.lib2.misc.random;

public record StaticRandom(long seed) {

	private static final long MULTIPLIER = 0x5DEECE66DL;
	private static final long ADDEND = 0xBL;
	private static final long MASK = (1L << 48) - 1;
	private static final float FLOAT_UNIT = 0x1.0p-24f; // 1.0f / (1 << Float.PRECISION)

	public StaticRandom() {
		this(System.nanoTime());
	}

	private static long next(long previous) {
		return (previous * MULTIPLIER + ADDEND) & MASK;
	}

	public float randomizeFloat(int... sequenceSeeds) {
		long s = seed;
		for (int i = 0; i < sequenceSeeds.length; i++) {
			s = next(s ^ next(sequenceSeeds[i]));
		}
		return (s >>> (48 - Float.PRECISION)) * FLOAT_UNIT;
	}

	public float randomizeFloat(int first, int second) {
		long s = next(next(first) ^ seed);
		s = next(s ^ next(second));
		return (s >>> (48 - Float.PRECISION)) * FLOAT_UNIT;
	}

	public float randomizeFloat(int first, int second, int third) {
		long s = next(next(first) ^ seed);
		s = next(s ^ next(second));
		s = next(s ^ next(third));
		return (s >>> (48 - Float.PRECISION)) * FLOAT_UNIT;
	}

	public float randomizeFloat(int first, int second, int third, int fourth) {
		long s = next(next(first) ^ seed);
		s = next(s ^ next(second));
		s = next(s ^ next(third));
		s = next(s ^ next(fourth));
		return (s >>> (48 - Float.PRECISION)) * FLOAT_UNIT;
	}

	public int randomizeInt(int... sequenceSeeds) {
		long s = seed;
		for (int i = 0; i < sequenceSeeds.length; i++) {
			s = next(s ^ next(sequenceSeeds[i]));
		}
		return (int) s;
	}

	public int randomizeInt(int first, int second) {
		long s = next(next(first) ^ seed);
		s = next(s ^ next(second));
		return (int) s;
	}

	public int randomizeInt(int first, int second, int third) {
		long s = next(next(first) ^ seed);
		s = next(s ^ next(second));
		s = next(s ^ next(third));
		return (int) s;
	}

	public int randomizeInt(int first, int second, int third, int fourth) {
		long s = next(next(first) ^ seed);
		s = next(s ^ next(second));
		s = next(s ^ next(third));
		s = next(s ^ next(fourth));
		return (int) s;
	}

	public Sequence newSequence(long sequenceSeed) {
		return new Sequence(next(sequenceSeed) ^ seed);
	}

	public static class Sequence {

		long sequenceState;

		private Sequence(long sequenceSeed) {
			this.sequenceState = next(sequenceSeed);
		}

		public int nextInt() {
			this.sequenceState = next(sequenceState);
			return (int) this.sequenceState;
		}

		public int nextInt(int limit) {
			return (nextInt() & Integer.MAX_VALUE) % limit;
		}
	}
}
