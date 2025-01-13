package net.skds.lib2.io.json.codec.array;

import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.SerializeOnlyJsonCodec;

import java.io.IOException;

public class ArraySerializeOnlyJsonCodec extends SerializeOnlyJsonCodec<Object> {

	private final JsonCodec<Object> writer;

	public ArraySerializeOnlyJsonCodec(Class<?> type, JsonCodecRegistry registry) {
		super(type, registry);
		this.writer = registry.getCodecIndirect(type);
	}

	@Override
	public final void write(Object value, JsonWriter writer) throws IOException {
		ArrayCodec.write(value, writer, this.writer);
	}
}
