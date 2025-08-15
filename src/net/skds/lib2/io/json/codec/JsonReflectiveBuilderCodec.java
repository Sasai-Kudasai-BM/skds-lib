package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class JsonReflectiveBuilderCodec<T> extends JsonAbstractJsonReflectiveBuilderCodec<T> {

	private final JsonSerializer<T> serializer;

	@SuppressWarnings("unchecked")
	public JsonReflectiveBuilderCodec(Type type, Type builderType, JsonCodecRegistry registry) {
		super(type, builderType, registry);
		while (!(type instanceof Class<?> cl)) {
			if (!(type instanceof ParameterizedType pt)) {
				throw new IllegalArgumentException("unsupported type \"" + type + "\"");
			}
			type = pt.getRawType();
		}
		this.serializer = (JsonSerializer<T>) ReflectiveJsonCodecFactory.INSTANCE.createSerializer(cl, registry);
	}

	@Override
	public final void write(T value, JsonWriter writer) throws IOException {
		this.serializer.write(value, writer);
	}

}
