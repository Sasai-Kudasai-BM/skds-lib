package net.skds.lib2.io;

import java.io.IOException;

public interface Codec<V, W, R> {

	void serialize(V value, W writer) throws IOException;

	V deserialize(R reader) throws IOException;

}
