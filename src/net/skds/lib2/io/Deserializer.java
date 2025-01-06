package net.skds.lib2.io;

import java.io.IOException;

public interface Deserializer<V, R> {
	
	V read(R reader) throws IOException;
}
