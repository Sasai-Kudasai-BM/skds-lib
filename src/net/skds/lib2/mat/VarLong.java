package net.skds.lib2.mat;

import lombok.AllArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

@AllArgsConstructor
public class VarLong extends Number implements Comparable<Long> {

	public final long value;

	@Override
	public String toString() {
		return Long.toString(value);
	}

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Number) {
			return value == ((Number) obj).longValue();
		}
		return false;
	}

	@Override
	public int compareTo(Long o) {
		return Long.compare(value, o);
	}

	@Override
	public int intValue() {
		return (int) value;
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

	public static long readFromBuffer(ByteBuffer buf) {
		// https://github.com/jvm-profiling-tools/async-profiler/blob/a38a375dc62b31a8109f3af97366a307abb0fe6f/src/converter/one/jfr/JfrReader.java#L404
		long result = 0;
		for (int shift = 0; shift < 56; shift += 7) {
			byte b = buf.get();
			result |= (b & 0x7fL) << shift;
			if (b >= 0) {
				return result;
			}
		}
		return result | (buf.get() & 0xffL) << 56;
	}

	public static void writeToBuffer(ByteBuffer buffer, long value) {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= (byte) 0b10000000;
			}
			buffer.put(temp);
		} while (value != 0);
	}

	public static long read(DataInput input) throws IOException {
		// https://github.com/jvm-profiling-tools/async-profiler/blob/a38a375dc62b31a8109f3af97366a307abb0fe6f/src/converter/one/jfr/JfrReader.java#L404
		long result = 0;
		for (int shift = 0; shift < 56; shift += 7) {
			byte b = input.readByte();
			result |= (b & 0x7fL) << shift;
			if (b >= 0) {
				return result;
			}
		}
		return result | (input.readByte() & 0xffL) << 56;
	}

	public static void write(DataOutput output, long value) throws IOException {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= (byte) 0b10000000;
			}
			output.writeByte(temp);
		} while (value != 0);
	}
}
