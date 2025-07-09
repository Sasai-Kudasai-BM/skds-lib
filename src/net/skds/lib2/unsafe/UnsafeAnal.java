package net.skds.lib2.unsafe;

import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Deprecated
@UtilityClass
@SuppressWarnings("unused")
public final class UnsafeAnal {

	public static final Unsafe UNSAFE = getUnsafe();

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
