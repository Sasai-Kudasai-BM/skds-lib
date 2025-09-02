package net.skds.lib2.io.codec.nulls;

import net.skds.lib2.io.codec.DeserializeOnlyCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;

import java.io.IOException;
import java.lang.reflect.Type;

public class NullDeserializeOnlyCodec extends DeserializeOnlyCodec<Object> {

	public NullDeserializeOnlyCodec(Type type, CodecRegistry registry) {
		super(type, registry);
	}

	public NullDeserializeOnlyCodec(CodecRegistry registry) {
		super(null, registry);
	}

	@Override
	public final Object read(UniversalReader reader) throws IOException {
		reader.skipNull();
		return null;
	}
}
