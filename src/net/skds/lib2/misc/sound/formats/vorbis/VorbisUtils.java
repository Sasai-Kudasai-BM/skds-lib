package net.skds.lib2.misc.sound.formats.vorbis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VorbisUtils {


	public static long lookup1Values(long entries, int dimensions) {
		if (dimensions <= 0 || entries <= 0) {
			return 0;
		}

		int returnValue = 0;

		// Use a while loop to find the largest integer N
		// such that N^dimensions <= entries.
		while (true) {
			int nextValue = returnValue + 1;

			// Calculate nextValue ^ dimensions safely
			long testPower = 1;
			boolean overflow = false;
			for (int i = 0; i < dimensions; i++) {
				// Check for multiplication overflow using Java 8+ utility
				if (Long.MAX_VALUE / nextValue < testPower) {
					overflow = true;
					break;
				}
				testPower *= nextValue;
			}

			if (overflow || testPower > entries) {
				// The next integer value is too large or caused overflow.
				break;
			}

			returnValue = nextValue;
		}

		return returnValue;
	}

	public static float intBits2Float(int bits) {
		float m = bits & 0x1fffff;
		if (bits < 0) m = -m;
		int e = (bits & 0x7fe00000) >>> 21;
		return m * (1 << (e - 788));
	}

	public static int iLog(int x) {
		if (x < 0) return 0;
		return Integer.SIZE - Integer.numberOfLeadingZeros(x);
	}
}
