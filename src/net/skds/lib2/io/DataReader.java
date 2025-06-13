package net.skds.lib2.io;

import java.io.IOException;

@FunctionalInterface
public interface DataReader<T> {
	T read(ExtendedDataInput input) throws IOException;
}
