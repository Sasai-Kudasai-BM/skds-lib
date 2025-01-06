package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;

import java.io.IOException;
import java.lang.reflect.Type;

public abstract class SerializeOnlyJsonCodec<T> extends AbstractJsonCodec<T> {

	public SerializeOnlyJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final T read(JsonReader reader) throws IOException {
		throw new UnsupportedOperationException("Deserialization is not supported for \"" + codecType + "\"");
	}
}
