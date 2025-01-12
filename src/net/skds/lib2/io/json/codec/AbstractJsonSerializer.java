package net.skds.lib2.io.json.codec;

public abstract class AbstractJsonSerializer<T> implements JsonSerializer<T> {
	final JsonCodecRegistry registry;

	protected AbstractJsonSerializer(JsonCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public JsonCodecRegistry getRegistry() {
		return registry;
	}
}
