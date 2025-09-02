package net.skds.lib2.io.codec.nulls;

import net.skds.lib2.io.codec.DeserializeOnlyCodec;
import net.skds.lib2.io.codec.UniversalCodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;

import java.io.IOException;
import java.lang.reflect.Type;

public class NullDeserializeOnlyCodec extends DeserializeOnlyCodec<Object> {

	public NullDeserializeOnlyCodec(Type type, UniversalCodecRegistry registry) {
		super(type, registry);
	}

	public NullDeserializeOnlyCodec(UniversalCodecRegistry registry) {
		super(null, registry);
	}

	@Override
	public final Object read(UniversalReader reader) throws IOException {
		reader.skipNull();
		return null;
	}
}
