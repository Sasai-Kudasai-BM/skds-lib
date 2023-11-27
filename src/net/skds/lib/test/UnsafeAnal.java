package net.skds.lib.test;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

public class UnsafeAnal {

	public static final Unsafe UNSAFE = getUnsafe();

	public static void main(String[] args) throws Exception {
		System.out.println("anal start");


		//UNSAFE.copyMemory(o, 0, data, UNSAFE.arrayBaseOffset(byte[].class), data.length);
		//UNSAFE.copyMemory(o, 0, data, UNSAFE.arrayBaseOffset(byte[].class), data.length);
		//String out = SKDSUtils.HEX_FORMAT_LC.formatHex(data);
		//System.out.println(out);

		double[] load = new double[1];
		

		System.out.println(Arrays.toString(load));

		System.out.println("ded");
	}

	private static Unsafe getUnsafe() {
		try {
			Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
			Field f = unsafeClass.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
