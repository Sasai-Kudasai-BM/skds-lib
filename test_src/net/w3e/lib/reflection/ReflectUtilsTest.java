package net.w3e.lib.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.skds.lib2.benchmark.Benchmark;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.utils.logger.SKDSLogger;

@SuppressWarnings("unused")
public class ReflectUtilsTest {

	public static void main(String[] args) throws IllegalAccessException, NoSuchMethodException, SecurityException {
		SKDSLogger.replaceOuts();

		Method method = ReflectUtilsTestNest.getCl().getDeclaredMethod("test");

		Benchmark benchmark1 = new Benchmark(100, 1000) {
			@Override
			protected void prepare() {
			}
			@Override
			protected void bench() {
				for (int i = 0; i < 1000; i++) {
					try {
						method.invoke(null);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		};

		benchmark1.run();
		System.out.println(benchmark1.result());

		MethodHandles.Lookup METHOD_LOOKUP = MethodHandles.lookup();
		MethodHandle handle = METHOD_LOOKUP.unreflect(method);

		Benchmark benchmark2 = new Benchmark(100, 1000) {
			@Override
			protected void prepare() {
			}
			@Override
			protected void bench() {
				for (int i = 0; i < 1000; i++) {
					try {
						handle.invoke();
					} catch (Throwable e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		};

		benchmark2.run();
		System.out.println(benchmark2.result());

		Benchmark benchmark3 = new Benchmark(100, 1000) {
			@Override
			protected void prepare() {
			}
			@Override
			protected void bench() {
				for (int i = 0; i < 1000; i++) {
					try {
						handle.invokeExact();
					} catch (Throwable e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		};

		benchmark3.run();
		System.out.println(benchmark3.result());
	}

	private static void test() {
		FastMath.sinDegr(1000);
	}

	private static void test(int a) {
		FastMath.sinDegr(a);
	}

	private static void test(String object) {
		FastMath.sinDegr(object.length());
	}

	private static void test(int a, int b, int c, int d) {
		FastMath.sinDegr(a + b + c + d);
	}

}
