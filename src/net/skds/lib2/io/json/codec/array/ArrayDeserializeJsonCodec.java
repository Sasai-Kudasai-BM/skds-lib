package net.skds.lib2.io.json.codec.array;

import java.io.IOException;
import java.lang.reflect.Array;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.io.json.exception.UnsupportedJsonSerializationException;

public class ArrayDeserializeJsonCodec extends AbstractJsonCodec<Object> {

	private final JsonCodec<Object> reader;
	private final Object[] array;

	public ArrayDeserializeJsonCodec(Class<?> type, JsonCodecRegistry registry) {
		super(type, registry);
		this.reader = registry.getCodecIndirect(type);
		this.array = (Object[]) Array.newInstance(type, 0);
	}

	@Override
	public final void write(Object value, JsonWriter writer) throws IOException {
		throw new UnsupportedJsonSerializationException(codecType);
	}

	@Override
	public final Object read(JsonReader reader) throws IOException {
		return ArrayCodec.read(reader, this.reader, this.array);
	}
}
