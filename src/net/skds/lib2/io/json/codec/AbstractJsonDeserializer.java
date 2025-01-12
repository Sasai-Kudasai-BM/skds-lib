package net.skds.lib2.io.json.codec;

public abstract class AbstractJsonDeserializer<T> implements JsonDeserializer<T> {
	final JsonCodecRegistry registry;

	protected AbstractJsonDeserializer(JsonCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public JsonCodecRegistry getRegistry() {
		return registry;
	}
}
