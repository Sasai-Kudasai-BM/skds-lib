package net.skds.lib2.io.json.codec.nulls;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.exception.UnsupportedJsonSerializationException;

public class NullSerializeJsonCodec extends AbstractJsonCodec<Object> {

	public NullSerializeJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	public NullSerializeJsonCodec(JsonCodecRegistry registry) {
		super(registry);
	}

	@Override
	public final void write(Object value, JsonWriter writer) throws IOException {
		writer.writeNull();
	}

	@Override
	public final Object read(JsonReader reader) throws IOException {
		throw new UnsupportedJsonSerializationException(codecType);
	}
}
