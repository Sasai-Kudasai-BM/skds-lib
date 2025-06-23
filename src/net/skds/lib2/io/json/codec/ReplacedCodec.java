package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class ReplacedCodec extends AbstractJsonCodec<Object> {

	private final JsonCodec<Object> codec;

	public ReplacedCodec(Type originalType, Type newType, JsonCodecRegistry registry) {
		super(originalType, registry);
		this.codec = this.registry.getCodecIndirect(newType);
	}

	@Override
	public final void write(Object value, JsonWriter writer) throws IOException {
		codec.write(value, writer);
	}

	@Override
	public final Object read(JsonReader reader) throws IOException {
		return this.codec.read(reader);
	}
}
