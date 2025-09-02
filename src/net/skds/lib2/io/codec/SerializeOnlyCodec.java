package net.skds.lib2.io.codec;

import net.skds.lib2.io.exception.UnsupportedJsonException;

import java.io.IOException;
import java.lang.reflect.Type;

public abstract class SerializeOnlyCodec<T> extends AbstractCodec<T> {

	public SerializeOnlyCodec(Type type, UniversalCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final T read(UniversalReader reader) throws IOException {
		throw new UnsupportedJsonException(codecType);
	}

	public static <T> UniversalCodec<T> ofSerializer(UniversalSerializer<T> serializer, Type type, UniversalCodecRegistry registry) {
		return new SerializeOnlyCodec<>(type, registry) {

			@Override
			public void write(T value, UniversalWriter writer) throws IOException {
				serializer.write(value, writer);
			}
		};
	}
}
