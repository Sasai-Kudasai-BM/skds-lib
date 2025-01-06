package net.skds.lib2.io.json.codec;

import java.lang.reflect.Type;

public abstract class AbstractJsonCodec<T> implements JsonCodec<T> {

	protected final JsonCodecRegistry registry;
	protected final Type codecType;

	public AbstractJsonCodec(Type type, JsonCodecRegistry registry) {
		this.codecType = type;
		this.registry = registry;
	}

	public AbstractJsonCodec(JsonCodecRegistry registry) {
		this.codecType = null;
		this.registry = registry;
	}

	@Override
	public final JsonCodecRegistry getRegistry() {
		return registry;
	}
}
