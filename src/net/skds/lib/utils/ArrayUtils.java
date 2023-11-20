package net.skds.lib.utils;

import java.lang.reflect.Array;
import java.util.Collection;

public class ArrayUtils {

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Collection<T> collection, Class<T> type) {
		return collection.toArray((T[]) Array.newInstance(type, collection.size()));
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] createGenericArray(Class<T> type, int size) {
		return (T[]) Array.newInstance(type, size);
	}

	public static void copySameSize(Object from, Object to, int size) {
		System.arraycopy(from, 0, to, 0, size);
	}

	public static <T> int find(T o, T[] array) {
		for (int i = 0; i < array.length; i++) {
			if (o.equals(array[i])) {
				return i;
			}
		}
		return -1;
	}

	public static byte loop(byte[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static boolean loop(boolean[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static short loop(short[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static char loop(char[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static int loop(int[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static long loop(long[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static float loop(float[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static double loop(double[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}

	public static <T> T loop(T[] array, int pos) {
		if (pos < 0) pos = -pos;
		return array[pos % array.length];
	}
}
