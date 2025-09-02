package net.skds.lib2.io.codec;

import net.skds.lib2.io.exception.UnsupportedDeserializationException;

import java.io.IOException;
import java.lang.reflect.Type;

public abstract class DeserializeOnlyCodec<T> extends AbstractCodec<T> {

	public DeserializeOnlyCodec(Type type, UniversalCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final void write(T value, UniversalWriter writer) throws IOException {
		throw new UnsupportedDeserializationException(codecType);
	}

	public static <T> UniversalCodec<T> ofDeserializer(UniversalDeserializer<T> deserializer, Type type, UniversalCodecRegistry registry) {
		return new DeserializeOnlyCodec<>(type, registry) {

			@Override
			public T read(UniversalReader reader) throws IOException {
				return deserializer.read(reader);
			}
		};
	}
}
