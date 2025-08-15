package net.skds.lib2.io.json.codec;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonReader;

public abstract class JsonAbstractJsonReflectiveBuilderCodec<T> extends AbstractJsonCodec<T> {

	private final JsonDeserializer<JsonDeserializeBuilder<T>> deserializer;

	public JsonAbstractJsonReflectiveBuilderCodec(Type type, Type builderType, JsonCodecRegistry registry) {
		super(type, registry);
		this.deserializer = registry.getDeserializerIndirect(builderType);
	}

	@Override
	public T read(JsonReader reader) throws IOException {
		return deserializer.read(reader).build();
	}
}
