package net.skds.lib2.io;

import java.io.IOException;

public interface Codec<V, W, R> {

	void write(V value, W writer) throws IOException;

	V read(R reader) throws IOException;

}
