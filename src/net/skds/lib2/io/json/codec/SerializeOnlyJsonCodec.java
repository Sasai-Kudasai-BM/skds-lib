package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public abstract class SerializeOnlyJsonCodec<T> extends AbstractJsonCodec<T> {

	public SerializeOnlyJsonCodec(Type type, JsonCodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public final T read(JsonReader reader) throws IOException {
		throw new UnsupportedOperationException("Deserialization is not supported for \"" + codecType + "\"");
	}

	public static <T> JsonCodec<T> ofSerializer(JsonSerializer<T> serializer, Type type, JsonCodecRegistry registry) {
		return new SerializeOnlyJsonCodec<>(type, registry) {

			@Override
			public void write(T value, JsonWriter writer) throws IOException {
				serializer.write(value, writer);
			}
		};
	}
}
