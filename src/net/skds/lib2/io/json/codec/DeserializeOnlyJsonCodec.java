package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.exception.UnsupportedJsonDeserializationException;

import java.io.IOException;
import java.lang.reflect.Type;

public abstract class DeserializeOnlyJsonCodec<T> extends AbstractJsonCodec<T> {

	public DeserializeOnlyJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final void write(T value, JsonWriter writer) throws IOException {
		throw new UnsupportedJsonDeserializationException(codecType);
	}

	public static <T> JsonCodec<T> ofDeserializer(JsonDeserializer<T> deserializer, Type type, JsonCodecRegistry registry) {
		return new DeserializeOnlyJsonCodec<>(type, registry) {

			@Override
			public T read(JsonReader reader) throws IOException {
				return deserializer.read(reader);
			}
		};
	}
}
