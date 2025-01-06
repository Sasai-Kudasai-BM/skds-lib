package net.skds.lib2.io.json.codec;

public abstract class AbstractJsonCodec<T> implements JsonCodec<T> {

	protected final JsonCodecRegistry registry;

	public AbstractJsonCodec(JsonCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public JsonCodecRegistry getRegistry() {
		return registry;
	}
}
