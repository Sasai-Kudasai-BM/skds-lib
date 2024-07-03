package net.skds.lib.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class UnsafeAnal {

	public static final Unsafe UNSAFE = getUnsafe();
	public static final int ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

	private UnsafeAnal() {
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
