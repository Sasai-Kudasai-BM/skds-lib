package net.skds.lib2.io.codec;

import java.lang.reflect.Type;

public abstract class AbstractCodec<T> implements UniversalCodec<T> {

	protected final CodecRegistry registry;
	protected final Type codecType;

	public AbstractCodec(Type type, CodecRegistry registry) {
		this.codecType = type;
		this.registry = registry;
	}

	public AbstractCodec(CodecRegistry registry) {
		this.codecType = null;
		this.registry = registry;
	}

	@Override
	public final CodecRegistry getRegistry() {
		return registry;
	}
}
