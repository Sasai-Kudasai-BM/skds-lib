package net.skds.lib2.io.codec.array;

import net.skds.lib2.io.codec.BuiltinCodecFactory.ArrayCodec;
import net.skds.lib2.io.codec.DeserializeOnlyCodec;
import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.reflection.ReflectUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class ArrayDeserializeOnlyCodec extends DeserializeOnlyCodec<Object> {

	private final UniversalCodec<Object> reader;
	private final Object[] array;

	public ArrayDeserializeOnlyCodec(Type type, CodecRegistry registry) {
		super(type, registry);
		this.reader = registry.getCodecIndirect(type);
		this.array = (Object[]) Array.newInstance(ReflectUtils.getRawType(type), 0);
	}

	@Override
	public final Object read(UniversalReader reader) throws IOException {
		return ArrayCodec.read(this.array, reader, this.reader);
	}
}
