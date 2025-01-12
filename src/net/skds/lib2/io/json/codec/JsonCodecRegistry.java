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
	private final Map<Type, JsonSerializer<?>> serializerMap = new ConcurrentHashMap<>();
	private final Map<Type, JsonDeserializer<?>> deserializerMap = new ConcurrentHashMap<>();
	private final Function<? super Type, ? extends JsonSerializer<?>> serializerMappingFunction;
	private final Function<? super Type, ? extends JsonDeserializer<?>> deserializerMappingFunction;
	private static final JsonCodecFactory builtin = BuiltinCodecFactory.INSTANCE;

	public JsonCodecRegistry(JsonCodecOptions options, JsonCodecFactory extraFactory) {
		this.options = options.clone();
		JsonCodecFactory combined;
		if (extraFactory != null) {
			combined = extraFactory.orElse(builtin);
		} else {
			combined = builtin;
		}
		this.serializerMappingFunction = t -> {
			JsonSerializer<?> s = combined.createSerializer(t, this);
			if (s instanceof JsonCodec<?> jc) deserializerMap.putIfAbsent(t, jc);
			return s;
		};
		this.deserializerMappingFunction = t -> {
			JsonDeserializer<?> d = combined.createDeserializer(t, this);
			if (d instanceof JsonCodec<?> jc) serializerMap.putIfAbsent(t, jc);
			return d;
		};
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
		JsonSerializer<?> serializer = serializerMap.computeIfAbsent(type, serializerMappingFunction);
		if (serializer instanceof JsonCodec<?>) {
			return (JsonCodec<T>) serializer;
		}
		JsonDeserializer<?> deserializer = deserializerMap.computeIfAbsent(type, deserializerMappingFunction);
		if (deserializer instanceof JsonCodec<?>) {
			return (JsonCodec<T>) deserializer;
		} else if (serializer != null && deserializer != null) {
			JsonCodec<?> codec = new CombinedJsonCodec<>(serializer, deserializer);
			serializerMap.put(type, codec);
			deserializerMap.put(type, codec);
			return (JsonCodec<T>) codec;
		} else if (type instanceof ParameterizedType pt) {
			return getCodec(pt.getRawType());
		}
		throw new RuntimeException("Unable to get codec for \"" + type + "\"");
	}


	@SuppressWarnings("unchecked")
	public <T> JsonSerializer<T> getSerializer(Type type) {
		JsonSerializer<?> serializer = serializerMap.computeIfAbsent(type, serializerMappingFunction);
		if (serializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getSerializer(pt.getRawType());
			}
			throw new RuntimeException("Unable to get serializer for \"" + type + "\"");
		}
		return (JsonSerializer<T>) serializer;
	}

	@SuppressWarnings("unchecked")
	public <T> JsonDeserializer<T> getDeserializer(Type type) {
		JsonDeserializer<?> deserializer = deserializerMap.computeIfAbsent(type, deserializerMappingFunction);
		if (deserializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getDeserializer(pt.getRawType());
			}
			throw new RuntimeException("Unable to get deserializer for \"" + type + "\"");
		}
		return (JsonDeserializer<T>) deserializer;
	}

	@SuppressWarnings("unchecked")
	public <T> JsonSerializer<T> getSerializerNullable(Type type) {
		JsonSerializer<?> serializer = serializerMap.computeIfAbsent(type, serializerMappingFunction);
		if (serializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getSerializer(pt.getRawType());
			}
			return null;
		}
		return (JsonSerializer<T>) serializer;
	}

	@SuppressWarnings("unchecked")
	public <T> JsonDeserializer<T> getDeserializerNullable(Type type) {
		JsonDeserializer<?> deserializer = deserializerMap.computeIfAbsent(type, deserializerMappingFunction);
		if (deserializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getDeserializer(pt.getRawType());
			}
			return null;
		}
		return (JsonDeserializer<T>) deserializer;
	}

	public <T> JsonSerializer<T> getSerializer(Class<T> type) {
		return getSerializer((Type) type);
	}

	public <T> JsonDeserializer<T> getDeserializer(Class<T> type) {
		return getDeserializer((Type) type);
	}

	public <T> JsonCodec<T> getCodecIndirect(Type type) {
		return new JsonCodec<>() {

			JsonCodec<T> codec;

			@Override
			public JsonCodecRegistry getRegistry() {
				return JsonCodecRegistry.this;
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

			@Override
			public void write(T value, JsonWriter writer) throws IOException {
				JsonCodec<T> c = codec;
				if (c == null) {
					c = getCodec(type);
					codec = c;
				}
				c.write(value, writer);
			}
		};
	}

	public <T> JsonDeserializer<T> getDeserializerIndirect(Type type) {
		return new JsonDeserializer<>() {

			JsonDeserializer<T> deserializer;

			@Override
			public JsonCodecRegistry getRegistry() {
				return JsonCodecRegistry.this;
			}

			@Override
			public T read(JsonReader reader) throws IOException {
				JsonDeserializer<T> c = deserializer;
				if (c == null) {
					c = getDeserializer(type);
					deserializer = c;
				}
				return c.read(reader);
			}
		};
	}

	public <T> JsonSerializer<T> getSerializerIndirect(Type type) {
		return new JsonSerializer<>() {

			JsonSerializer<T> serializer;

			@Override
			public JsonCodecRegistry getRegistry() {
				return JsonCodecRegistry.this;
			}

			@Override
			public void write(T value, JsonWriter writer) throws IOException {
				JsonSerializer<T> c = serializer;
				if (c == null) {
					c = getSerializer(type);
					serializer = c;
				}
				c.write(value, writer);
			}
		};
	}
}
