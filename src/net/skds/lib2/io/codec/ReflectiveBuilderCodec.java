package net.skds.lib2.io.codec;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ReflectiveBuilderCodec<T> extends AbstractCodec<T> {

	private final UniversalDeserializer<DeserializeBuilder<T>> deserializer;
	private final UniversalSerializer<T> serializer;

	@SuppressWarnings("unchecked")
	public ReflectiveBuilderCodec(Type type, Type builderType, CodecRegistry registry) {
		super(type, registry);
		while (!(type instanceof Class<?> cl)) {
			if (!(type instanceof ParameterizedType pt)) {
				throw new IllegalArgumentException("unsupported type \"" + type + "\"");
			}
			type = pt.getRawType();
		}
		this.deserializer = registry.getDeserializerIndirect(builderType);
		this.serializer = (UniversalSerializer<T>) ReflectiveCodecFactory.INSTANCE.createSerializer(cl, registry);
	}

	@Override
	public T read(UniversalReader reader) throws IOException {
		return deserializer.read(reader).build();
	}

	@Override
	public void write(T value, UniversalWriter writer) throws IOException {
		serializer.write(value, writer);
	}
}
