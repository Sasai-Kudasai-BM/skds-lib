package net.skds.lib2.utils;

import net.skds.lib2.mat.FastMath;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class ArrayUtils {

	public static final byte[] EMPTY_BYTE = {};
	public static final boolean[] EMPTY_BOOLEAN = {};
	public static final char[] EMPTY_CHAR = {};
	public static final short[] EMPTY_SHORT = {};
	public static final int[] EMPTY_INT = {};
	public static final float[] EMPTY_FLOAT = {};
	public static final long[] EMPTY_LONG = {};
	public static final double[] EMPTY_DOUBLE = {};
	public static final Object[] EMPTY_OBJECT = {};

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

	public static int loop(int pos, int length) {
		int ret = pos % length;
		if (ret < 0) ret += length;
		return ret;
	}

	public static byte loop(int pos, byte[] array) {
		return array[loop(pos, array.length)];
	}

	public static boolean loop(int pos, boolean[] array) {
		return array[loop(pos, array.length)];
	}

	public static short loop(int pos, short[] array) {
		return array[loop(pos, array.length)];
	}

	public static char loop(int pos, char[] array) {
		return array[loop(pos, array.length)];
	}

	public static int loop(int pos, int[] array) {
		return array[loop(pos, array.length)];
	}

	public static long loop(int pos, long[] array) {
		return array[loop(pos, array.length)];
	}

	public static float loop(int pos, float[] array) {
		return array[loop(pos, array.length)];
	}

	public static double loop(int pos, double[] array) {
		return array[loop(pos, array.length)];
	}

	@Deprecated
	public static <T> T loop(T[] array, int pos) {
		return array[loop(pos, array.length)];
	}

	public static <T> T loop(int pos, T[] array) {
		return array[loop(pos, array.length)];
	}

	public static <T> T loop(int pos, List<T> list) {
		return list.get(loop(pos, list.size()));
	}

	public static <T> T getRandom(List<T> list) {
		return list.get(FastMath.RANDOM.nextInt(list.size()));
	}

	public static <T> T getRandom(T[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static byte getRandom(byte[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static boolean getRandom(boolean[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static short getRandom(short[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static char getRandom(char[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static int getRandom(int[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static float getRandom(float[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static long getRandom(long[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static double getRandom(double[] array) {
		return array[FastMath.RANDOM.nextInt(array.length)];
	}

	public static class ObjHashedArray {

		public final Object[] array;
		private final int hash;

		public ObjHashedArray(Object[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof ObjHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class IntHashedArray {

		public final int[] array;
		private final int hash;

		public IntHashedArray(int[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof IntHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class ByteHashedArray {

		public final byte[] array;
		private final int hash;

		public ByteHashedArray(byte[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof ByteHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class CharHashedArray {

		public final char[] array;
		private final int hash;

		public CharHashedArray(char[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof CharHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class ShortHashedArray {

		public final short[] array;
		private final int hash;

		public ShortHashedArray(short[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof ShortHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class FloatHashedArray {

		public final float[] array;
		private final int hash;

		public FloatHashedArray(float[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof FloatHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class DoubleHashedArray {

		public final double[] array;
		private final int hash;

		public DoubleHashedArray(double[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof DoubleHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class LongHashedArray {

		public final long[] array;
		private final int hash;

		public LongHashedArray(long[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof LongHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static class BooleanHashedArray {

		public final boolean[] array;
		private final int hash;

		public BooleanHashedArray(boolean[] array) {
			this.array = array;
			this.hash = Arrays.hashCode(array);
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o instanceof BooleanHashedArray ah) {
				return Arrays.equals(array, ah.array);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public static final class ByteGrowingArray {
		private int pos = 0;
		private byte[] array;

		public ByteGrowingArray(int initialSize) {
			this.array = new byte[initialSize];
		}

		public void add(byte value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public byte[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}

	public static final class BooleanGrowingArray {
		private int pos = 0;
		private boolean[] array;

		public BooleanGrowingArray(int initialSize) {
			this.array = new boolean[initialSize];
		}

		public void add(boolean value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public boolean[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}

	public static final class ShortGrowingArray {
		private int pos = 0;
		private short[] array;

		public ShortGrowingArray(int initialSize) {
			this.array = new short[initialSize];
		}

		public void add(short value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public short[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}

	public static final class CharGrowingArray {
		private int pos = 0;
		private char[] array;

		public CharGrowingArray(int initialSize) {
			this.array = new char[initialSize];
		}

		public void add(char value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public char[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}

	public static final class IntGrowingArray {
		private int pos = 0;
		private int[] array;

		public IntGrowingArray(int initialSize) {
			this.array = new int[initialSize];
		}

		public void add(int value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public int[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}

	public static final class LongGrowingArray {
		private int pos = 0;
		private long[] array;

		public LongGrowingArray(int initialSize) {
			this.array = new long[initialSize];
		}

		public void add(long value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public long[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}

	public static final class FloatGrowingArray {
		private int pos = 0;
		private float[] array;

		public FloatGrowingArray(int initialSize) {
			this.array = new float[initialSize];
		}

		public void add(float value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public float[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}

	public static final class DoubleGrowingArray {
		private int pos = 0;
		private double[] array;

		public DoubleGrowingArray(int initialSize) {
			this.array = new double[initialSize];
		}

		public void add(double value) {
			if (++pos >= array.length) {
				array = Arrays.copyOf(array, array.length * 2);
			}
			array[pos - 1] = value;
		}

		public double[] getArray() {
			return Arrays.copyOf(array, pos);
		}
	}
}
