package net.skds.lib2.io.codec;

import java.io.IOException;
import java.util.UUID;

public interface UniversalWriter {

	void beginObject() throws IOException;

	default void beginObject(String name) throws IOException {
		writeName(name);
		beginObject();
	}

	void endObject() throws IOException;

	void beginList() throws IOException;

	default void beginList(String name) throws IOException {
		writeName(name);
		beginList();
	}

	void endList() throws IOException;

	void writeName(String name) throws IOException;

	void writeString(String s) throws IOException;

	void writeRaw(String s) throws IOException;

	default void writeString(String name, String s) throws IOException {
		writeName(name);
		writeString(s);
	}

	void writeBoolean(boolean b) throws IOException;

	default void writeBoolean(String name, boolean b) throws IOException {
		writeName(name);
		writeBoolean(b);
	}

	void writeNull() throws IOException;

	default void writeNull(String name) throws IOException {
		writeName(name);
		writeNull();
	}

	void writeLong(long n) throws IOException;

	default void writeLong(String name, long n) throws IOException {
		writeName(name);
		writeLong(n);
	}

	//void writeTime(long n) throws IOException;
	//default void writeTime(String name, long n) throws IOException {
	//	writeName(name);
	//	writeTime(n);
	//}

	void writeInt(int n) throws IOException;

	default void writeInt(String name, int n) throws IOException {
		writeName(name);
		writeInt(n);
	}

	void writeShort(short n) throws IOException;

	void writeByte(byte n) throws IOException;

	void writeHex(long n) throws IOException;

	default void writeHex(String name, long n) throws IOException {
		writeName(name);
		writeHex(n);
	}

	void writeHex(int n) throws IOException;

	void writeDouble(double n) throws IOException;

	void writeFloat(float n) throws IOException;

	default void writeDouble(String name, double n) throws IOException {
		writeName(name);
		writeDouble(n);
	}

	default void writeFloat(String name, float n) throws IOException {
		writeName(name);
		writeFloat(n);
	}

	default void writeDoubleExp(String name, double n) throws IOException {
		writeName(name);
		writeDoubleExp(n);
	}

	default void writeFloatExp(String name, float n) throws IOException {
		writeName(name);
		writeFloatExp(n);
	}

	void writeDoubleExp(double n) throws IOException;

	void writeFloatExp(float n) throws IOException;

	void writeByteArray(byte[] b) throws IOException;

	default void writeByteArray(String name, byte[] b) throws IOException {
		writeName(name);
		writeByteArray(b);
	}

	void writeCharArray(char[] arr) throws IOException;

	default void writeCharArray(String name, char[] arr) throws IOException {
		writeName(name);
		writeCharArray(arr);
	}

	void writeShortArray(short[] arr) throws IOException;

	default void writeShortArray(String name, short[] arr) throws IOException {
		writeName(name);
		writeShortArray(arr);
	}

	void writeIntArray(int[] arr) throws IOException;

	default void writeIntArray(String name, int[] arr) throws IOException {
		writeName(name);
		writeIntArray(arr);
	}

	void writeLongArray(long[] arr) throws IOException;

	default void writeLongArray(String name, long[] arr) throws IOException {
		writeName(name);
		writeLongArray(arr);
	}

	void writeFloatArray(float[] arr) throws IOException;

	default void writeFloatArray(String name, float[] arr) throws IOException {
		writeName(name);
		writeFloatArray(arr);
	}

	void writeDoubleArray(double[] arr) throws IOException;

	default void writeDoubleArray(String name, double[] arr) throws IOException {
		writeName(name);
		writeDoubleArray(arr);
	}

	void writeUUID(UUID uuid) throws IOException;

	default void writeUUID(String name, UUID uuid) throws IOException {
		writeName(name);
		writeUUID(uuid);
	}

	void writeComment(String comment) throws IOException;

	void lineBreakEnable(boolean lineBreak) throws IOException;

}
