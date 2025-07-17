package net.skds.lib2.natives;

import lombok.experimental.UtilityClass;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.List;

/**
 * <p>AKA "Safe UnsafeAnal", "SafeAnal"</p>
 * (I LOVE anal (both roles))
 */
@UtilityClass
@SuppressWarnings("unused")
public class MemoryAccess {

	private static final VarHandle VH_BYTE = ValueLayout.JAVA_BYTE.varHandle();
	private static final VarHandle VH_SHORT = ValueLayout.JAVA_SHORT.varHandle();
	private static final VarHandle VH_CHAR = ValueLayout.JAVA_CHAR.varHandle();
	private static final VarHandle VH_INT = ValueLayout.JAVA_INT.varHandle();
	private static final VarHandle VH_LONG = ValueLayout.JAVA_LONG.varHandle();
	private static final VarHandle VH_FLOAT = ValueLayout.JAVA_FLOAT.varHandle();
	private static final VarHandle VH_DOUBLE = ValueLayout.JAVA_DOUBLE.varHandle();

	public static final ValueLayout.OfBoolean BOOLEAN = ValueLayout.JAVA_BOOLEAN;
	public static final ValueLayout.OfByte BYTE = ValueLayout.JAVA_BYTE;
	public static final ValueLayout.OfShort SHORT = ValueLayout.JAVA_SHORT;
	public static final ValueLayout.OfInt INT = ValueLayout.JAVA_INT;
	public static final ValueLayout.OfLong LONG = ValueLayout.JAVA_LONG;
	public static final ValueLayout.OfLong PTR = ValueLayout.JAVA_LONG;
	public static final ValueLayout.OfFloat FLOAT = ValueLayout.JAVA_FLOAT;
	public static final ValueLayout.OfDouble DOUBLE = ValueLayout.JAVA_DOUBLE;

	public static final MemorySegment ALL_MEMORY = MemorySegment.NULL.reinterpret((1L << 63) - 1);

	public static long loadArray(Arena arena, byte... values) {
		return arena.allocateFrom(BYTE, values).address();
	}

	public static long loadArray(Arena arena, short... values) {
		return arena.allocateFrom(SHORT, values).address();
	}

	public static long loadArray(Arena arena, int... values) {
		return arena.allocateFrom(INT, values).address();
	}

	public static long loadArray(Arena arena, float... values) {
		return arena.allocateFrom(FLOAT, values).address();
	}

	public static long loadArray(Arena arena, long... values) {
		return arena.allocateFrom(LONG, values).address();
	}

	public static long loadArray(Arena arena, double... values) {
		return arena.allocateFrom(DOUBLE, values).address();
	}

	public static long loadCString(Arena arena, String string) {
		return arena.allocateFrom(string).address();
	}

	public static MemorySegment loadCString(String string) {
		return Arena.ofAuto().allocateFrom(string);
	}

	public static long loadCStringArray(Arena arena, List<String> strings) {
		int count = strings.size();
		long[] pointers = new long[count];
		for (int i = 0; i < count; i++) {
			pointers[i] = arena.allocateFrom(strings.get(i)).address();
		}
		return arena.allocateFrom(ValueLayout.JAVA_LONG, pointers).address();
	}

	public static MemorySegment loadCStringArray(List<String> strings) {
		Arena arena = Arena.ofAuto();
		int count = strings.size();
		long[] pointers = new long[count];
		for (int i = 0; i < count; i++) {
			pointers[i] = arena.allocateFrom(strings.get(i)).address();
		}
		return arena.allocateFrom(ValueLayout.JAVA_LONG, pointers);
	}

	public static long loadCStringArray(Arena arena, String... strings) {
		int count = strings.length;
		long[] pointers = new long[count];
		for (int i = 0; i < count; i++) {
			pointers[i] = arena.allocateFrom(strings[i]).address();
		}
		return arena.allocateFrom(ValueLayout.JAVA_LONG, pointers).address();
	}

	public static MemorySegment loadCStringArray(String... strings) {
		Arena arena = Arena.ofAuto();
		int count = strings.length;
		long[] pointers = new long[count];
		for (int i = 0; i < count; i++) {
			pointers[i] = arena.allocateFrom(strings[i]).address();
		}
		return arena.allocateFrom(ValueLayout.JAVA_LONG, pointers);
	}


	public static String[] getCStringArray(MemorySegment segment, long offset, int count) {
		long[] pointers = new long[count];
		String[] strings = new String[count];
		MemorySegment.copy(segment, LONG, offset, pointers, 0, count);
		for (int i = 0; i < count; i++) {
			strings[i] = segment.getString(pointers[i]);
		}
		return strings;
	}

	public static MemorySegment wrapSegment(long address, long size) {
		return MemorySegment.ofAddress(address).reinterpret(size);
	}

	public static MemorySegment allocSlices(Arena arena, MemoryLayout... sizes) {
		long alignment = 0;
		for (int i = 0; i < sizes.length; i++) {
			MemoryLayout ml = sizes[i];
			alignment += calcPadding(alignment, (int) ml.byteAlignment()) + ml.byteSize();
		}
		return arena.allocate(alignment, 8);
	}

	public static long[] allocSlices(Arena arena, int... sizes) {
		long totalSize = 0;
		final long[] arr = new long[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			int s = sizes[i];
			totalSize += calcPadding(totalSize, Math.min(s, 8));
			arr[i] = totalSize;
			totalSize += s;
		}
		MemorySegment segment = arena.allocate(totalSize);
		long address = segment.address();
		for (int i = 0; i < arr.length; i++) {
			arr[i] += address;
		}
		return arr;
	}

	public static long alloc4(Arena arena, int count) {
		return arena.allocate(count, 4).address();
	}

	public static long alloc2(Arena arena, int count) {
		return arena.allocate(count, 2).address();
	}

	public static long alloc8(Arena arena, int count) {
		return arena.allocate(count, 8).address();
	}

	public static long[] allocPointers(Arena arena, int count) {
		long totalSize = count * 8L;
		final long[] arr = new long[count];
		MemorySegment segment = arena.allocate(totalSize);
		long address = segment.address();
		for (int i = 0; i < count; i++) {
			arr[i] = address + i * 8L;
		}
		return arr;
	}

	public static long[] slices(MemorySegment segment, MemoryLayout... sizes) {
		final long[] arr = new long[sizes.length];
		long alignment = segment.address();
		for (int i = 0; i < arr.length; i++) {
			MemoryLayout ml = sizes[i];
			alignment += calcPadding(alignment, (int) ml.byteAlignment());
			arr[i] = alignment;
			alignment += ml.byteSize();
		}
		return arr;
	}

	public static long[] slices(MemorySegment segment, int... sizes) {
		final long[] arr = new long[sizes.length];
		long alignment = segment.address();
		for (int i = 0; i < arr.length; i++) {
			int s = sizes[i];
			alignment += calcPadding(alignment, s);
			arr[i] = alignment;
			alignment += s;
		}
		return arr;
	}

	public static int calcPadding(long address, long alignment) {
		int rem = (int) (address % alignment);
		return rem == 0 ? 0 : (int) (alignment - rem);
	}

	public static void setByte(MemorySegment ms, long offset, byte value) {
		ms.set(ValueLayout.JAVA_BYTE, offset, value);
	}

	public static void setByte(MemorySegment ms, long offset, int index, byte value) {
		ms.set(ValueLayout.JAVA_BYTE, offset + index, value);
	}

	public static void setByte(long address, byte value) {
		VH_BYTE.set(ALL_MEMORY, address, value);
	}

	public static void setByte(long address, int index, byte value) {
		VH_BYTE.set(ALL_MEMORY, address + index, value);
	}

	public static void setShort(MemorySegment ms, long offset, short value) {
		ms.set(ValueLayout.JAVA_SHORT, offset, value);
	}

	public static void setShort(MemorySegment ms, long offset, int index, short value) {
		ms.set(ValueLayout.JAVA_SHORT, offset + index * 2L, value);
	}

	public static void setShort(long address, short value) {
		VH_SHORT.set(ALL_MEMORY, address, value);
	}

	public static void setShort(long address, int index, short value) {
		VH_SHORT.set(ALL_MEMORY, address + index * 2L, value);
	}

	public static void setChar(MemorySegment ms, long offset, char value) {
		ms.set(ValueLayout.JAVA_CHAR, offset, value);
	}

	public static void setChar(MemorySegment ms, long offset, int index, char value) {
		ms.set(ValueLayout.JAVA_CHAR, offset + index * 2L, value);
	}

	public static void setChar(long address, char value) {
		VH_CHAR.set(ALL_MEMORY, address, value);
	}

	public static void setChar(long address, int index, char value) {
		VH_CHAR.set(ALL_MEMORY, address + index * 2L, value);
	}

	public static void setInt(MemorySegment ms, long offset, int value) {
		ms.set(ValueLayout.JAVA_INT, offset, value);
	}

	public static void setInt(MemorySegment ms, long offset, int index, int value) {
		ms.set(ValueLayout.JAVA_INT, offset + index * 4L, value);
	}

	public static void setInt(long address, int value) {
		VH_INT.set(ALL_MEMORY, address, value);
	}

	public static void setInt(long address, int index, int value) {
		VH_INT.set(ALL_MEMORY, address + index * 4L, value);
	}

	public static void setLong(MemorySegment ms, long offset, long value) {
		ms.set(ValueLayout.JAVA_LONG, offset, value);
	}

	public static void setLong(MemorySegment ms, long offset, int index, long value) {
		ms.set(ValueLayout.JAVA_LONG, offset + index * 8L, value);
	}

	public static void setLong(long address, long value) {
		VH_LONG.set(ALL_MEMORY, address, value);
	}

	public static void setLong(long address, int index, long value) {
		VH_LONG.set(ALL_MEMORY, address + index * 8L, value);
	}

	public static void setFloat(MemorySegment ms, long offset, float value) {
		ms.set(ValueLayout.JAVA_FLOAT, offset, value);
	}

	public static void setFloat(MemorySegment ms, long offset, int index, float value) {
		ms.set(ValueLayout.JAVA_FLOAT, offset + index * 4L, value);
	}

	public static void setFloat(long address, float value) {
		VH_FLOAT.set(ALL_MEMORY, address, value);
	}

	public static void setFloat(long address, int index, float value) {
		VH_FLOAT.set(ALL_MEMORY, address + index * 4L, value);
	}

	public static void setDouble(MemorySegment ms, long offset, double value) {
		ms.set(ValueLayout.JAVA_DOUBLE, offset, value);
	}

	public static void setDouble(MemorySegment ms, long offset, int index, double value) {
		ms.set(ValueLayout.JAVA_DOUBLE, offset + index * 8L, value);
	}

	public static void setDouble(long address, double value) {
		VH_DOUBLE.set(ALL_MEMORY, address, value);
	}

	public static void setDouble(long address, int index, double value) {
		VH_DOUBLE.set(ALL_MEMORY, address + index * 8L, value);
	}

	public static byte getByte(MemorySegment ms, long offset) {
		return (byte) VH_BYTE.get(ms, offset);
	}

	public static byte getByte(MemorySegment ms, long offset, int index) {
		return (byte) VH_BYTE.get(ms, offset + index);
	}

	public static byte getByte(long address) {
		return (byte) VH_BYTE.get(ALL_MEMORY, address);
	}

	public static byte getByte(long address, int index) {
		return (byte) VH_BYTE.get(ALL_MEMORY, address + index);
	}

	public static short getShort(MemorySegment ms, long offset) {
		return (short) VH_SHORT.get(ms, offset);
	}

	public static short getShort(MemorySegment ms, long offset, int index) {
		return (short) VH_SHORT.get(ms, offset + index * 2L);
	}

	public static short getShort(long address) {
		return (short) VH_SHORT.get(ALL_MEMORY, address);
	}

	public static short getShort(long address, int index) {
		return (short) VH_SHORT.get(ALL_MEMORY, address + index * 2L);
	}

	public static char getChar(MemorySegment ms, long offset) {
		return (char) VH_CHAR.get(ms, offset);
	}

	public static char getChar(MemorySegment ms, long offset, int index) {
		return (char) VH_CHAR.get(ms, offset + index * 2L);
	}

	public static char getChar(long address) {
		return (char) VH_CHAR.get(ALL_MEMORY, address);
	}

	public static char getChar(long address, int index) {
		return (char) VH_CHAR.get(ALL_MEMORY, address + index * 2L);
	}

	public static int getInt(MemorySegment ms, long offset) {
		return (int) VH_INT.get(ms, offset);
	}

	public static int getInt(MemorySegment ms, long offset, int index) {
		return (int) VH_INT.get(ms, offset + index * 4L);
	}

	public static int getInt(long address) {
		return (int) VH_INT.get(ALL_MEMORY, address);
	}

	public static int getInt(long address, int index) {
		return (int) VH_INT.get(ALL_MEMORY, address + index * 4L);
	}

	public static long getLong(MemorySegment ms, long offset) {
		return (long) VH_LONG.get(ms, offset);
	}

	public static long getLong(MemorySegment ms, long offset, int index) {
		return (long) VH_LONG.get(ms, offset + index * 8L);
	}

	public static long getLong(long address) {
		return (long) VH_LONG.get(ALL_MEMORY, address);
	}

	public static long getLong(long address, int index) {
		return (long) VH_LONG.get(ALL_MEMORY, address + index * 8L);
	}

	public static float getFloat(MemorySegment ms, long offset) {
		return (float) VH_FLOAT.get(ms, offset);
	}

	public static float getFloat(MemorySegment ms, long offset, int index) {
		return (float) VH_FLOAT.get(ms, offset + index * 4L);
	}

	public static float getFloat(long address) {
		return (float) VH_FLOAT.get(ALL_MEMORY, address);
	}

	public static float getFloat(long address, int index) {
		return (float) VH_FLOAT.get(ALL_MEMORY, address + index * 4L);
	}

	public static double getDouble(MemorySegment ms, long offset) {
		return (double) VH_DOUBLE.get(ms, offset);
	}

	public static double getDouble(MemorySegment ms, long offset, int index) {
		return (double) VH_DOUBLE.get(ms, offset + index * 8L);
	}

	public static double getDouble(long address) {
		return (double) VH_DOUBLE.get(ALL_MEMORY, address);
	}

	public static double getDouble(long address, int index) {
		return (double) VH_DOUBLE.get(ALL_MEMORY, address + index * 8L);
	}
}
