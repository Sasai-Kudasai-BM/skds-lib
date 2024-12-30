package net.skds.lib2.io.json;

import java.io.IOException;

public interface JsonReader {

	JsonEntryType nextEntryType() throws IOException;

	void readDotDot() throws IOException;

	String readName() throws IOException;

	void beginObject() throws IOException;

	void endObject() throws IOException;

	void beginArray() throws IOException;

	void endArray() throws IOException;

	boolean readBoolean() throws IOException;

	void skipNull() throws IOException;

	Number readNumber() throws IOException;

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

	String readString() throws IOException;
}
