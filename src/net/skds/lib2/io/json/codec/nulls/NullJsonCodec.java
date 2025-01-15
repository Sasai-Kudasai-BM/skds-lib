package net.skds.lib2.io.json.codec.nulls;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

public class NullJsonCodec extends AbstractJsonCodec<Object> {

	public NullJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	public NullJsonCodec(JsonCodecRegistry registry) {
		super(registry);
	}

	@Override
	public void write(Object value, JsonWriter writer) throws IOException {
		writer.writeNull();
	}

	@Override
	public Object read(JsonReader reader) throws IOException {
		reader.skipNull();
		return null;
	}
}
