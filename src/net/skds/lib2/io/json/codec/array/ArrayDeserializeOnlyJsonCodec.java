package net.skds.lib2.io.json.codec.array;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.io.json.codec.DeserializeOnlyJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.lang.reflect.Array;

public class ArrayDeserializeOnlyJsonCodec extends DeserializeOnlyJsonCodec<Object> {

	private final JsonCodec<Object> reader;
	private final Object[] array;

	public ArrayDeserializeOnlyJsonCodec(Class<?> type, JsonCodecRegistry registry) {
		super(type, registry);
		this.reader = registry.getCodecIndirect(type);
		this.array = (Object[]) Array.newInstance(type, 0);
	}
	
	@Override
	public final Object read(JsonReader reader) throws IOException {
		return ArrayCodec.read(this.array, reader, this.reader);
	}
}
