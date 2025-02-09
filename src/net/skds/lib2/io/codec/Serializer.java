package net.skds.lib2.io.codec;

import java.io.IOException;

public interface Serializer<V, W> {
	void write(V value, W writer) throws IOException;
}
