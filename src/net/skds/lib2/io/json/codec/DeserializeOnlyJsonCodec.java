package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public abstract class DeserializeOnlyJsonCodec<T> implements JsonCodec<T> {

	protected final JsonCodecRegistry registry;

	public DeserializeOnlyJsonCodec(JsonCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public JsonCodecRegistry getRegistry() {
		return registry;
	}

	@Override
	public void write(T value, JsonWriter writer) throws IOException {
		throw new UnsupportedOperationException();
	}
}
