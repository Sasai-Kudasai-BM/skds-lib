package net.skds.lib2.io.codec.nulls;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class NullCodec extends AbstractCodec<Object> {

	public NullCodec(Type type, CodecRegistry registry) {
		super(type, registry);
	}

	public NullCodec(CodecRegistry registry) {
		super(registry);
	}

	@Override
	public void write(Object value, UniversalWriter writer) throws IOException {
		writer.writeNull();
	}

	@Override
	public Object read(UniversalReader reader) throws IOException {
		reader.skipNull();
		return null;
	}
}
