package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.CharOutput;
import net.skds.lib2.io.json.*;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JsonCodecRegistry {

	final JsonCodecOptions options;
	private final Map<Type, JsonCodec<?>> codecMap = new ConcurrentHashMap<>();
	private final Function<? super Type, ? extends JsonCodec<?>> mappingFunction;
	private static final JsonCodecFactory builtin = BuiltinCodecFactory.INSTANCE;

	public JsonCodecRegistry(JsonCodecOptions options, JsonCodecFactory extraFactory) {
		this.options = options.clone();
		JsonCodecFactory combined;
		if (extraFactory != null) {
			combined = extraFactory.orElse(builtin);
		} else {
			combined = builtin;
		}
		this.mappingFunction = t -> combined.createCodec(t, this);
	}

	public JsonReader createReader(CharInput input) {
		return new JsonReaderImpl(input, this);
	}

	public JsonWriter createWriter(CharOutput output) {
		return switch (options.getDecorationType()) {
			case FANCY -> new FormattedJsonWriterImpl(output, options.getTabulation());
			default -> new FlatJsonWriterImpl(output);
		};
	}

	@SuppressWarnings("unchecked")
	public <T> JsonCodec<T> getCodec(Type type) {
		JsonCodec<?> codec = codecMap.computeIfAbsent(type, mappingFunction);
		if (codec == null) {
			if (type instanceof ParameterizedType pt) {
				return getCodec(pt.getRawType());
			}
			throw new RuntimeException("Unable to get json codec for \"" + type + "\"");
		}
		return (JsonCodec<T>) codec;
	}

	public <T> JsonCodec<T> getCodec(Class<T> type) {
		return getCodec((Type) type);
	}

	public <T> JsonCodec<T> getCodecIndirect(Type type) {
		return new AbstractJsonCodec<>(this) {

			JsonCodec<T> codec;

			@Override
			public void write(T value, JsonWriter writer) throws IOException {
				JsonCodec<T> c = codec;
				if (c == null) {
					c = getCodec(type);
					codec = c;
				}
				c.write(value, writer);
			}

			@Override
			public T read(JsonReader reader) throws IOException {
				JsonCodec<T> c = codec;
				if (c == null) {
					c = getCodec(type);
					codec = c;
				}
				return c.read(reader);
			}
		};
	}

}
