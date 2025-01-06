package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class JsonReflectiveBuilderCodec<T> extends AbstractJsonCodec<T> {

	private final JsonCodec<JsonDeserializeBuilder<T>> deserializer;
	private final JsonSerializer<T> serializer;

	@SuppressWarnings("unchecked")
	public JsonReflectiveBuilderCodec(Type type, Type builderType, JsonCodecRegistry registry) {
		super(type, registry);
		while (!(type instanceof Class<?> cl)) {
			if (!(type instanceof ParameterizedType pt)) {
				throw new IllegalArgumentException("unsupported type \"" + type + "\"");
			}
			type = pt.getRawType();
		}
		this.deserializer = registry.getCodecIndirect(builderType);
		this.serializer = BuiltinCodecFactory.INSTANCE.reflectiveFactory.getReflectiveSerializer((Class<T>) cl, registry);
	}

	@Override
	public T read(JsonReader reader) throws IOException {
		return deserializer.read(reader).build();
	}

	@Override
	public void write(T value, JsonWriter writer) throws IOException {
		serializer.write(value, writer);
	}
}
