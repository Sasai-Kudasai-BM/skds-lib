package net.skds.lib2.io;

import java.io.IOException;

public interface Serializer<V, W> {

	void write(V value, W writer) throws IOException;

}
