package net.skds.lib2.io.json.codec.array;

import java.io.IOException;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.io.json.exception.UnsupportedJsonDeserializationException;

public class ArraySerializeJsonCodec extends AbstractJsonCodec<Object> {

	private final JsonCodec<Object> writer;

	public ArraySerializeJsonCodec(Class<?> type, JsonCodecRegistry registry) {
		super(type, registry);
		this.writer = registry.getCodecIndirect(type);
	}

	@Override
	public final void write(Object value, JsonWriter writer) throws IOException {
		ArrayCodec.write(value, writer, this.writer);
	}

	@Override
	public final Object read(JsonReader reader) throws IOException {
		throw new UnsupportedJsonDeserializationException(codecType);
	}
}
