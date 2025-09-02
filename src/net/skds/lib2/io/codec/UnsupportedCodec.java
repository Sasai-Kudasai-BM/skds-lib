package net.skds.lib2.io.codec;

import net.skds.lib2.io.exception.UnsupportedJsonException;

import java.io.IOException;
import java.lang.reflect.Type;

public final class UnsupportedCodec<T> extends AbstractCodec<T> {

	public UnsupportedCodec(Type type, UniversalCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public void write(T value, UniversalWriter writer) throws IOException {
		throw new UnsupportedJsonException(codecType);
	}

	@Override
	public T read(UniversalReader reader) throws IOException {
		throw new UnsupportedJsonException(codecType);
	}
}
