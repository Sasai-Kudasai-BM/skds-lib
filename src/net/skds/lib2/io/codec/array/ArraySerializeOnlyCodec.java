package net.skds.lib2.io.codec.array;

import net.skds.lib2.io.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.io.codec.SerializeOnlyCodec;
import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.lib2.io.codec.UniversalCodecRegistry;
import net.skds.lib2.io.codec.UniversalWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class ArraySerializeOnlyCodec extends SerializeOnlyCodec<Object> {

	private final UniversalCodec<Object> writer;

	public ArraySerializeOnlyCodec(Type type, UniversalCodecRegistry registry) {
		super(type, registry);
		this.writer = registry.getCodecIndirect(type);
	}

	@Override
	public final void write(Object value, UniversalWriter writer) throws IOException {
		ArrayCodec.write(value, writer, this.writer);
	}
}
