package net.skds.lib2.io.json;

import net.skds.lib2.io.json.codec.JsonCapabilityVersion;

import java.io.IOException;

public interface JsonWriter {

	JsonCapabilityVersion capabilityVersion();

	void beginObject() throws IOException;

	default void beginObject(String name) throws IOException {
		writeName(name);
		beginObject();
	}

	void endObject() throws IOException;

	void beginArray() throws IOException;

	default void beginArray(String name) throws IOException {
		writeName(name);
		beginArray();
	}

	void endArray() throws IOException;

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

	void writeInt(long n) throws IOException;

	default void writeInt(String name, long n) throws IOException {
		writeName(name);
		writeInt(n);
	}

	void writeHex(long n) throws IOException;

	default void writeHex(String name, long n) throws IOException {
		writeName(name);
		writeHex(n);
	}

	void writeFloat(double n) throws IOException;

	default void writeFloat(String name, double n) throws IOException {
		writeName(name);
		writeFloat(n);
	}

	void writeFloatExp(double n) throws IOException;

	default void writeFloatExp(String name, double n) throws IOException {
		writeName(name);
		writeFloatExp(n);
	}

	void writeComment(String comment) throws IOException;

	void setLineBreakEnable(boolean lineBreak) throws IOException;

}
