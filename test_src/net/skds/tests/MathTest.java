package net.skds.tests;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.utils.logger.SKDSLogger;

public class MathTest {


	static void main() {
		SKDSLogger.replaceOuts();

		for (float i = -2; i < 2; i += 0.1f) {
			System.out.println(i + " -> " + FastMath.round(i));
			test(FastMath.round(i) == Math.round(i));
		}
	}

	private static void test(boolean result) {
		if (!result) throw new RuntimeException();
	}
}
