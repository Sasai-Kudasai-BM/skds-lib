package net.skds.lib2.io.codec;

import java.lang.reflect.Type;

public abstract class AbstractCodec<T> implements UniversalCodec<T> {

	protected final UniversalCodecRegistry registry;
	protected final Type codecType;

	public AbstractCodec(Type type, UniversalCodecRegistry registry) {
		this.codecType = type;
		this.registry = registry;
	}

	public AbstractCodec(UniversalCodecRegistry registry) {
		this.codecType = null;
		this.registry = registry;
	}

	@Override
	public final UniversalCodecRegistry getRegistry() {
		return registry;
	}
}
