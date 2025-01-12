package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public final class UnsupportedJsonCodec<T> extends AbstractJsonCodec<T> {

	public UnsupportedJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public void write(T value, JsonWriter writer) throws IOException {
		throw new UnsupportedOperationException("Serialization is not supported for \"" + codecType + "\"");
	}

	@Override
	public T read(JsonReader reader) throws IOException {
		throw new UnsupportedOperationException("Deserialization is not supported for \"" + codecType + "\"");
	}
}
