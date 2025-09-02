package net.skds.lib2.io.codec;

import java.io.IOException;
import java.lang.reflect.Type;


public abstract class AbstractReflectiveBuilderCodec<T> extends AbstractCodec<T> {

	private final UniversalDeserializer<DeserializeBuilder<T>> deserializer;

	public AbstractReflectiveBuilderCodec(Type type, Type builderType, CodecRegistry registry) {
		super(type, registry);
		this.deserializer = registry.getDeserializerIndirect(builderType);
	}

	@Override
	public T read(UniversalReader reader) throws IOException {
		return deserializer.read(reader).build();
	}
}
