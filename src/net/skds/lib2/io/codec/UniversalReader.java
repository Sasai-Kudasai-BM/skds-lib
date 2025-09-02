package net.skds.lib2.io.codec;


import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.util.UUID;

public interface UniversalReader {

	SosisonEntryType nextEntryType() throws IOException;

	default SosisonEntryType getArrayTypeIfSupported() throws IOException {
		return null;
	}

	String readName() throws IOException;

	void beginObject() throws IOException;

	void endObject() throws IOException;

	void beginList() throws IOException;

	void endList() throws IOException;

	boolean readBoolean() throws IOException;

	void skipNull() throws IOException;

	void skipValue() throws IOException;

	Number readNumber() throws IOException;

	default byte readByte() throws IOException {
		return readNumber().byteValue();
	}

	default short readShort() throws IOException {
		return readNumber().shortValue();
	}

	default int readInt() throws IOException {
		return readNumber().intValue();
	}

	default long readLong() throws IOException {
		return readNumber().longValue();
	}

	default float readFloat() throws IOException {
		return readNumber().floatValue();
	}

	default double readDouble() throws IOException {
		return readNumber().doubleValue();
	}

	default long readTime() throws IOException {
		return readNumber().longValue();
	}

	String readString() throws IOException;

	byte[] readByteArray() throws IOException;

	short[] readShortArray() throws IOException;

	char[] readCharArray() throws IOException;

	int[] readIntArray() throws IOException;

	long[] readLongArray() throws IOException;

	float[] readFloatArray() throws IOException;

	double[] readDoubleArray() throws IOException;

	UUID readUUID() throws IOException;
}
