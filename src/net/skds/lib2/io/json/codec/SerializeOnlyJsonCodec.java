package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;

import java.io.IOException;

public abstract class SerializeOnlyJsonCodec<T> extends AbstractJsonCodec<T> {

	public SerializeOnlyJsonCodec(JsonCodecRegistry registry) {
		super(registry);
	}

	@Override
	public final T read(JsonReader reader) throws IOException {
		throw new UnsupportedOperationException();
	}
}
