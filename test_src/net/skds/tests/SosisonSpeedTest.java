package net.skds.tests;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.skds.lib2.benchmark.Benchmark;
import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.lib2.utils.logger.SKDSLoggerConfig;

public class SosisonSpeedTest {

	// 60-70 ms

	static void main() {
		SKDSLoggerConfig.compactConfig();
		SKDSLogger.replaceOuts();
		System.out.println("start...");
		Test0 test0 = new Test0("t0", 1488, 322f, .01);
		Test1 test1 = new Test1("t0", 1488, 322f, .01);

		System.out.println(Benchmark.runSimple(() -> {
			var o0 = test0;
			var o1 = test1;
			for (int i = 0; i < 10 * 128; i++) {
				String j0 = SosisonUtils.toJson(test0);
				String j1 = SosisonUtils.toJson(test1);
				o0 = SosisonUtils.parseJson(j0, Test0.class);
				o1 = SosisonUtils.parseJson(j1, Test1.class);
			}
			System.out.println(o0);
			System.out.println(o1);
		}));

	}

	private record Test0(String str, int i, float f, double d) {
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	private static class Test1 {
		String str;
		int i;
		float f;
		double d;
	}

	@ToString
	private static class Test2 {
		int[] array;
	}
}
