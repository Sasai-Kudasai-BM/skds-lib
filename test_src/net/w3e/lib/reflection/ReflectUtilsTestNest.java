package net.w3e.lib.reflection;

import net.skds.lib2.mat.FastMath;

@SuppressWarnings("unused")
public class ReflectUtilsTestNest {

	private static class InnerReflectUtilsTestNest {

		private static void test() {
			FastMath.sinDegr(1000);
		}

	}

	public static Class<?> getCl() {
		return InnerReflectUtilsTestNest.class;
	}
}
