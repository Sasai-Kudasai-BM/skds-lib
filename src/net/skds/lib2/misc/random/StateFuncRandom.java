package net.skds.lib2.misc.random;

public class StateFuncRandom {

	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;
	private static final long mask = (1L << 48) - 1;
	private static final float FLOAT_UNIT = 0x1.0p-24f; // 1.0f / (1 << Float.PRECISION)

	private final long seed;

	public StateFuncRandom(long seed) {
		this.seed = seed;
	}

	public StateFuncRandom() {
		this.seed = System.nanoTime();
	}

	private long next(long previous) {
		return (previous * multiplier + addend) & mask;
	}

	//public float randomize(Object first, Object... sequenceSeeds) {
	//	long s = next(first.hashCode());
	//	if (sequenceSeeds != null) {
	//		for (int i = 0; i < sequenceSeeds.length; i++) {
	//			s = next(s + sequenceSeeds[i].hashCode());
	//		}
	//	}
	//	return (s >>> (48 - Float.PRECISION)) * FLOAT_UNIT;
	//}

	public float randomize(int first, int... sequenceSeeds) {
		long s = next(next(first) ^ seed);
		if (sequenceSeeds != null) {
			for (int i = 0; i < sequenceSeeds.length; i++) {
				s = next(s ^ next(sequenceSeeds[i]) & mask);
			}
		}
		return (s >>> (48 - Float.PRECISION)) * FLOAT_UNIT;
	}

	public Sequence newSequence(long sequenceSeed) {
		return new Sequence(sequenceSeed);
	}

	public Sequence newSequence(Object... seeds) {
		return new Sequence(seeds);
	}

	public class Sequence {

		long sequenceState;

		private Sequence(Object... sequenceSeeds) {
			long s = seed;
			for (int i = 0; i < sequenceSeeds.length; i++) {
				s = next(s ^ sequenceSeeds[i].hashCode());
			}
			this.sequenceState = s;
		}

		private Sequence(long... sequenceSeeds) {
			long s = seed;
			for (int i = 0; i < sequenceSeeds.length; i++) {
				s = next(s ^ sequenceSeeds[i]);
			}
			this.sequenceState = s;
		}

		private Sequence(long sequenceSeed) {
			this.sequenceState = next(seed ^ sequenceSeed);
		}

		public int nextInt() {
			long next = next(sequenceState);
			return (int) (next >>> (16));
		}

	}

}
