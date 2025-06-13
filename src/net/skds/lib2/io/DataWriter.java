package net.skds.lib2.io;

import java.io.IOException;

@FunctionalInterface
public interface DataWriter<T> {
	void write(ExtendedDataOutput output, T value) throws IOException;
}
