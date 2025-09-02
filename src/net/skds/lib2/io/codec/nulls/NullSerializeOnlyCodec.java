package net.skds.lib2.io.codec.nulls;

import net.skds.lib2.io.codec.SerializeOnlyCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class NullSerializeOnlyCodec extends SerializeOnlyCodec<Object> {

	public NullSerializeOnlyCodec(Type type, CodecRegistry registry) {
		super(type, registry);
	}

	public NullSerializeOnlyCodec(CodecRegistry registry) {
		super(null, registry);
	}

	@Override
	public final void write(Object value, UniversalWriter writer) throws IOException {
		writer.writeNull();
	}

}
