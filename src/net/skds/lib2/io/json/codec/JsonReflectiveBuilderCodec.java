package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public abstract class JsonReflectiveBuilderCodec<T> extends AbstractJsonCodec<T> {

	private final JsonCodec<JsonDeserializeBuilder<T>> deserializer;
	private final JsonCodec<T> serializer;

	public JsonReflectiveBuilderCodec(Type type, Type builderType, JsonCodecRegistry registry) {
		super(registry);
		this.deserializer = registry.getCodecIndirect(builderType);
		this.serializer = registry.getCodecIndirect(type);
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
