package net.skds.lib2.io.json;

import net.skds.lib2.io.json.codec.JsonCapabilityVersion;

import java.io.IOException;

public interface JsonWriter {

	JsonCapabilityVersion capabilityVersion();

	void beginObject() throws IOException;

	void endObject() throws IOException;

	void beginArray() throws IOException;

	void endArray() throws IOException;

	void separate() throws IOException;

	void writeName(String name) throws IOException;

	void writeString(String s) throws IOException;

	void writeBoolean(boolean b) throws IOException;

	void writeNull() throws IOException;

	void writeInt(long n) throws IOException;

	void writeHex(long n) throws IOException;

	void writeFloat(double n) throws IOException;

	void writeFloatExp(double n) throws IOException;

	void writeComment(String comment) throws IOException;

	void pushLine() throws IOException;

	void space() throws IOException;
}
