package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;

import java.io.IOException;

public abstract class SerializeOnlyJsonCodec<T> implements JsonCodec<T> {

	protected final JsonCodecRegistry registry;

	public SerializeOnlyJsonCodec(JsonCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public JsonCodecRegistry getRegistry() {
		return registry;
	}


	@Override
	public T read(JsonReader reader) throws IOException {
		throw new UnsupportedOperationException();
	}
}
