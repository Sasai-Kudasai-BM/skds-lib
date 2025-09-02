package net.skds.lib2.io.codec;

import java.io.IOException;

public final class CombinedCodec<T> implements UniversalCodec<T> {

	private final UniversalCodecRegistry registry;
	private final UniversalSerializer<T> serializer;
	private final UniversalDeserializer<T> deserializer;

	@SuppressWarnings("unchecked")
	public CombinedCodec(UniversalSerializer<?> serializer, UniversalDeserializer<?> deserializer) {
		UniversalCodecRegistry registry = serializer.getRegistry();
		if (deserializer.getRegistry() != registry)
			throw new IllegalArgumentException("serializer and deserializer registries are not the same");
		this.registry = registry;
		this.serializer = (UniversalSerializer<T>) serializer;
		this.deserializer = (UniversalDeserializer<T>) deserializer;
	}

	@Override
	public UniversalCodecRegistry getRegistry() {
		return registry;
	}

	@Override
	public T read(UniversalReader reader) throws IOException {
		return deserializer.read(reader);
	}

	@Override
	public void write(T value, UniversalWriter writer) throws IOException {
		serializer.write(value, writer);
	}

	@Override
	public String valueAsKeyString(T val) {
		return serializer.valueAsKeyString(val);
	}
}
