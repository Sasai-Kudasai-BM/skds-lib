package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public abstract class DeserializeOnlyJsonCodec<T> extends AbstractJsonCodec<T> {

	public DeserializeOnlyJsonCodec(JsonCodecRegistry registry) {
		super(registry);
	}

	@Override
	public final void write(T value, JsonWriter writer) throws IOException {
		throw new UnsupportedOperationException();
	}
}
