package net.skds.lib2.io.codec;

import lombok.RequiredArgsConstructor;
import net.skds.lib2.io.ExtendedDataInput;
import net.skds.lib2.io.ExtendedDataOutput;
import net.skds.lib2.io.chars.CharInput;
import net.skds.lib2.io.chars.CharOutput;
import net.skds.lib2.io.json.FlatJsonWriter;
import net.skds.lib2.io.json.FormattedJsonWriter;
import net.skds.lib2.io.json.JsonReaderImpl;
import net.skds.lib2.io.json.WrappedJsonReaderImpl;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.sosison.SosisonReader;
import net.skds.lib2.io.sosison.SosisonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static net.skds.lib2.io.json.JsonCodecOptions.DecorationType.FANCY;

public class CodecRegistry {

	final UniversalCodecOptions options;
	private final Map<Type, UniversalCodec<?>> codecMap = new ConcurrentHashMap<>();
	private final Map<Type, UniversalSerializer<?>> serializerMap = new ConcurrentHashMap<>();
	private final Map<Type, UniversalDeserializer<?>> deserializerMap = new ConcurrentHashMap<>();
	private final Function<? super Type, ? extends UniversalCodec<?>> codecMappingFunction;
	private final Function<? super Type, ? extends UniversalSerializer<?>> serializerMappingFunction;
	private final Function<? super Type, ? extends UniversalDeserializer<?>> deserializerMappingFunction;
	private static final CodecFactory builtin = BuiltinCodecFactory.INSTANCE;

	public CodecRegistry(UniversalCodecOptions options, CodecFactory extraFactory) {
		this.options = options.clone();
		CodecFactory combined;
		if (extraFactory != null) {
			combined = extraFactory.orElse(builtin);
		} else {
			combined = builtin;
		}
		this.serializerMappingFunction = t -> combined.createSerializer(t, this);
		this.deserializerMappingFunction = t -> combined.createDeserializer(t, this);
		this.codecMappingFunction = t -> {
			UniversalSerializer<?> serializer = serializerMap.computeIfAbsent(t, serializerMappingFunction);
			if (serializer instanceof UniversalCodec<?>) {
				return (UniversalCodec<?>) serializer;
			}
			UniversalDeserializer<?> deserializer = deserializerMap.computeIfAbsent(t, deserializerMappingFunction);
			if (deserializer instanceof UniversalCodec<?>) {
				return (UniversalCodec<?>) deserializer;
			} else if (serializer != null && deserializer != null) {
				UniversalCodec<?> codec = new CombinedCodec<>(serializer, deserializer);
				return (UniversalCodec<?>) codec;
			}
			return combined.createCodec(t, this);
		};
	}

	public UniversalReader createReader(CharInput input) {
		return new JsonReaderImpl(input, this);
	}

	public UniversalReader createReader(ExtendedDataInput input) {
		return new SosisonReader(input);
	}

	public UniversalReader createReader(JsonElement input) {
		return new WrappedJsonReaderImpl(input);
	}

	public UniversalWriter createJsonWriter(CharOutput output) {
		if (options.getDecorationType() == FANCY) {
			return new FormattedJsonWriter(output, options.getTabulation(), options.getCapabilityVersion());

		}
		return new FlatJsonWriter(output);
	}

	public UniversalWriter createSosisonWriter(ExtendedDataOutput output) {
		return new SosisonWriter(output);
	}

	//public UniversalWriter createWriter(OutputStream output) {
	//	return switch (options.getDecorationType()) {
	//		case FANCY -> new FormattedJsonWriterImpl(output, options.getTabulation(), options.getCapabilityVersion());
	//		default -> new FlatJsonWriterImpl(output);
	//	};
	//}

	@SuppressWarnings("unchecked")
	public <T> UniversalCodec<T> getCodec(Type type) {
		UniversalCodec<T> codec = (UniversalCodec<T>) codecMap.computeIfAbsent(type, codecMappingFunction);
		if (codec == null) throw new RuntimeException("Unable to get codec for \"" + type + "\"");
		return codec;
	}


	@SuppressWarnings("unchecked")
	public <T> UniversalSerializer<T> getSerializer(Type type) {
		UniversalSerializer<?> serializer = serializerMap.computeIfAbsent(type, serializerMappingFunction);
		if (serializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getSerializer(pt.getRawType());
			}
			throw new RuntimeException("Unable to get serializer for \"" + type + "\"");
		}
		return (UniversalSerializer<T>) serializer;
	}

	@SuppressWarnings("unchecked")
	public <T> UniversalDeserializer<T> getDeserializer(Type type) {
		UniversalDeserializer<?> deserializer = deserializerMap.computeIfAbsent(type, deserializerMappingFunction);
		if (deserializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getDeserializer(pt.getRawType());
			}
			throw new RuntimeException("Unable to get deserializer for \"" + type + "\"");
		}
		return (UniversalDeserializer<T>) deserializer;
	}

	@SuppressWarnings("unchecked")
	public <T> UniversalSerializer<T> getSerializerNullable(Type type) {
		UniversalSerializer<?> serializer = serializerMap.computeIfAbsent(type, serializerMappingFunction);
		if (serializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getSerializer(pt.getRawType());
			}
			return null;
		}
		return (UniversalSerializer<T>) serializer;
	}

	@SuppressWarnings("unchecked")
	public <T> UniversalDeserializer<T> getDeserializerNullable(Type type) {
		UniversalDeserializer<?> deserializer = deserializerMap.computeIfAbsent(type, deserializerMappingFunction);
		if (deserializer == null) {
			if (type instanceof ParameterizedType pt) {
				return getDeserializer(pt.getRawType());
			}
			return null;
		}
		return (UniversalDeserializer<T>) deserializer;
	}

	public <T> UniversalSerializer<T> getSerializer(Class<T> type) {
		return getSerializer((Type) type);
	}

	public <T> UniversalDeserializer<T> getDeserializer(Class<T> type) {
		return getDeserializer((Type) type);
	}

	public <T> UniversalCodec<T> getCodecIndirect(Type type) {
		return new CodecIndirect<>(type);
	}

	public <T> UniversalDeserializer<T> getDeserializerIndirect(Type type) {
		return new DeserializerIndirect<>(type);
	}

	public <T> UniversalSerializer<T> getSerializerIndirect(Type type) {
		return new SerializerIndirect<>(type);
	}

	@RequiredArgsConstructor
	private class CodecIndirect<T> implements UniversalCodec<T> {

		private final Type type;

		private UniversalCodec<T> codec;

		private UniversalCodec<T> getOrCreateCodec() {
			UniversalCodec<T> c = codec;
			if (c == null) {
				c = getCodec(type);
				codec = c;
			}
			return c;
		}

		@Override
		public String valueAsKeyString(T val) {
			return getOrCreateCodec().valueAsKeyString(val);
		}

		@Override
		public T stringKeyToValue(String key) throws IOException {
			return getOrCreateCodec().stringKeyToValue(key);
		}

		@Override
		public CodecRegistry getRegistry() {
			return CodecRegistry.this;
		}

		@Override
		public T read(UniversalReader reader) throws IOException {
			return getOrCreateCodec().read(reader);
		}

		@Override
		public void write(T value, UniversalWriter writer) throws IOException {
			getOrCreateCodec().write(value, writer);
		}

		@Override
		public String toString() {
			return super.toString() + "(" + type + ")";
		}
	}

	@RequiredArgsConstructor
	private class DeserializerIndirect<T> implements UniversalDeserializer<T> {

		private final Type type;

		private UniversalDeserializer<T> deserializer;

		private UniversalDeserializer<T> getOrCreateCodec() {
			UniversalDeserializer<T> c = deserializer;
			if (c == null) {
				c = getDeserializer(type);
				deserializer = c;
			}
			return c;
		}

		@Override
		public T stringKeyToValue(String key) throws IOException {
			return getOrCreateCodec().stringKeyToValue(key);
		}

		@Override
		public CodecRegistry getRegistry() {
			return CodecRegistry.this;
		}

		@Override
		public T read(UniversalReader reader) throws IOException {
			return getOrCreateCodec().read(reader);
		}

		@Override
		public String toString() {
			return super.toString() + "(" + type + ")";
		}
	}

	@RequiredArgsConstructor
	private class SerializerIndirect<T> implements UniversalSerializer<T> {

		private final Type type;

		private UniversalSerializer<T> serializer;

		private UniversalSerializer<T> getOrCreateCodec() {
			UniversalSerializer<T> c = serializer;
			if (c == null) {
				c = getSerializer(type);
				serializer = c;
			}
			return c;
		}

		@Override
		public String valueAsKeyString(T val) {
			return getOrCreateCodec().valueAsKeyString(val);
		}

		@Override
		public CodecRegistry getRegistry() {
			return CodecRegistry.this;
		}

		@Override
		public void write(T value, UniversalWriter writer) throws IOException {
			getOrCreateCodec().write(value, writer);
		}

		@Override
		public String toString() {
			return super.toString() + "(" + type + ")";
		}
	}

}
