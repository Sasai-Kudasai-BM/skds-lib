package net.skds.lib2.mat;

import net.skds.lib.benchmark.Benchmark;
import net.skds.lib.mat.Vec3;

public class VecDemo {


	public static void main(String[] args) {
		var benchmark = new Benchmark(1_000, 1_000) {

			double r = 0;

			@Override
			public void prepare() {
			}

			@Override
			public void bench() {
				double val = 0;
				for (int i = 0; i < 1000; i++) {
					Vec3D vec = new Vec3D(0, .1, i + val);
					val += vec.cross(Vec3D.XP).sub(i, 2, -3).cross(Vec3D.XP).dot(vec);
				}
				r += val;
			}
		};
		var benchmark2 = new Benchmark(1_000, 1_000) {

			double r = 0;

			@Override
			public void prepare() {
			}

			@Override
			public void bench() {
				double val = 0;
				for (int i = 0; i < 1000; i++) {
					Vec3 vec = new Vec3(0, .1, i + val);
					val += vec.cross(Vec3.XP).sub(i, 2, -3).cross(Vec3.XP).dot(vec);
				}
				r += val;

			}
		};
		benchmark.run();
		benchmark2.run();

		System.out.println(benchmark.r);
		System.out.println(benchmark2.r);
		System.out.println(benchmark.result());
		System.out.println(benchmark2.result());
	}
}
