package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public abstract class DeserializeOnlyJsonCodec<T> extends AbstractJsonCodec<T> {

	public DeserializeOnlyJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final void write(T value, JsonWriter writer) throws IOException {
		throw new UnsupportedOperationException("Serialization is not supported for \"" + codecType + "\"");
	}
}
