package net.skds.lib2.io.json.codec.nulls;

import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.SerializeOnlyJsonCodec;

import java.io.IOException;
import java.lang.reflect.Type;

public class NullSerializeOnlyJsonCodec extends SerializeOnlyJsonCodec<Object> {

	public NullSerializeOnlyJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	public NullSerializeOnlyJsonCodec(JsonCodecRegistry registry) {
		super(null, registry);
	}

	@Override
	public final void write(Object value, JsonWriter writer) throws IOException {
		writer.writeNull();
	}

}
