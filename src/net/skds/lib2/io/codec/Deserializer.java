package net.skds.lib2.io.codec;

import java.io.IOException;

public interface Deserializer<V, R> {

	V read(R reader) throws IOException;
}
