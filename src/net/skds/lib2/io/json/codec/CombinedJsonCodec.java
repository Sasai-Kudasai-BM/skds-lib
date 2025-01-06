package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public final class CombinedJsonCodec<T> implements JsonCodec<T> {

	private final JsonCodecRegistry registry;
	private final JsonSerializer<T> serializer;
	private final JsonDeserializer<T> deserializer;

	@SuppressWarnings("unchecked")
	public CombinedJsonCodec(JsonSerializer<?> serializer, JsonDeserializer<?> deserializer) {
		JsonCodecRegistry registry = serializer.getRegistry();
		if (deserializer.getRegistry() != registry)
			throw new IllegalArgumentException("serializer and deserializer registries are not the same");
		this.registry = registry;
		this.serializer = (JsonSerializer<T>) serializer;
		this.deserializer = (JsonDeserializer<T>) deserializer;
	}

	@Override
	public JsonCodecRegistry getRegistry() {
		return registry;
	}

	@Override
	public T read(JsonReader reader) throws IOException {
		return deserializer.read(reader);
	}

	@Override
	public void write(T value, JsonWriter writer) throws IOException {
		serializer.write(value, writer);
	}

	@Override
	public String valueAsKeyString(T val) {
		return serializer.valueAsKeyString(val);
	}
}
