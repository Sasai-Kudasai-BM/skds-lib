package net.skds.lib2.io.json.codec.nulls;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.codec.DeserializeOnlyJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.lang.reflect.Type;

public class NullDeserializeOnlyJsonCodec extends DeserializeOnlyJsonCodec<Object> {

	public NullDeserializeOnlyJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	public NullDeserializeOnlyJsonCodec(JsonCodecRegistry registry) {
		super(null, registry);
	}

	@Override
	public final Object read(JsonReader reader) throws IOException {
		reader.skipNull();
		return null;
	}
}
