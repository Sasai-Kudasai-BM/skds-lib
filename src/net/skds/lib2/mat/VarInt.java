package net.skds.lib2.mat;

import lombok.AllArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

@AllArgsConstructor
public class VarInt extends Number implements Comparable<Integer> {

	public final int value;

	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Number) {
			return value == ((Number) obj).intValue();
		}
		return false;
	}

	@Override
	public int compareTo(Integer o) {
		return Integer.compare(value, o);
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	public static int readFromBuffer(ByteBuffer buf) {
		// https://github.com/jvm-profiling-tools/async-profiler/blob/a38a375dc62b31a8109f3af97366a307abb0fe6f/src/converter/one/jfr/JfrReader.java#L393
		int result = 0;
		for (int shift = 0; ; shift += 7) {
			byte b = buf.get();
			result |= (b & 0x7f) << shift;
			if (b >= 0) {
				return result;
			}
		}
	}

	public static int read(DataInput input) throws IOException {
		int result = 0;
		for (int shift = 0; ; shift += 7) {
			byte b = input.readByte();
			result |= (b & 0x7f) << shift;
			if (b >= 0) {
				return result;
			}
		}
	}

	public static void writeToBuffer(ByteBuffer buf, int value) {
		if ((value & (0xFFFFFFFF << 7)) == 0) {
			buf.put((byte) value);
		} else if ((value & (0xFFFFFFFF << 14)) == 0) {
			buf.putShort((short) ((value & 0x7F | 0x80) << 8 | (value >>> 7)));
		} else if ((value & (0xFFFFFFFF << 21)) == 0) {
			buf.put((byte) (value & 0x7F | 0x80));
			buf.put((byte) ((value >>> 7) & 0x7F | 0x80));
			buf.put((byte) (value >>> 14));
		} else if ((value & (0xFFFFFFFF << 28)) == 0) {
			buf.putInt((value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
					| ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21));
		} else {
			buf.putInt((value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
					| ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80));
			buf.put((byte) (value >>> 28));
		}
	}

	public static void write(DataOutput output, int value) throws IOException {
		if ((value & (0xFFFFFFFF << 7)) == 0) {
			output.writeByte((byte) value);
		} else if ((value & (0xFFFFFFFF << 14)) == 0) {
			output.writeShort((short) ((value & 0x7F | 0x80) << 8 | (value >>> 7)));
		} else if ((value & (0xFFFFFFFF << 21)) == 0) {
			output.writeByte((byte) (value & 0x7F | 0x80));
			output.writeByte((byte) ((value >>> 7) & 0x7F | 0x80));
			output.writeByte((byte) (value >>> 14));
		} else if ((value & (0xFFFFFFFF << 28)) == 0) {
			output.writeInt((value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
					| ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21));
		} else {
			output.writeInt((value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
					| ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80));
			output.writeByte((byte) (value >>> 28));
		}
	}

	public static int getSize(int input) {
		return (input & 0xFFFFFF80) == 0
				? 1 : (input & 0xFFFFC000) == 0
				? 2 : (input & 0xFFE00000) == 0
				? 3 : (input & 0xF0000000) == 0
				? 4 : 5;
	}

}
