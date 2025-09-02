package net.skds.lib2.io.codec;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface CodecFactory {

	UniversalCodec<?> createCodec(Type type, CodecRegistry registry);

	default UniversalSerializer<?> createSerializer(Type type, CodecRegistry registry) {
		return createCodec(type, registry);
	}

	default UniversalDeserializer<?> createDeserializer(Type type, CodecRegistry registry) {
		return createCodec(type, registry);
	}

	default CodecFactory orElse(CodecFactory other) {
		return new CodecFactory() {
			@Override
			public UniversalCodec<?> createCodec(Type type, CodecRegistry registry) {
				UniversalCodec<?> codec = CodecFactory.this.createCodec(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createCodec(type, registry);
			}

			@Override
			public UniversalSerializer<?> createSerializer(Type type, CodecRegistry registry) {
				UniversalSerializer<?> codec = CodecFactory.this.createSerializer(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createSerializer(type, registry);
			}

			@Override
			public UniversalDeserializer<?> createDeserializer(Type type, CodecRegistry registry) {
				UniversalDeserializer<?> codec = CodecFactory.this.createDeserializer(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createDeserializer(type, registry);
			}
		};
	}

	public static MapJsonFactory newMapFactory() {
		return new MapJsonFactory(new ConcurrentHashMap<>());
	}

	public final class MapJsonFactory implements CodecFactory {

		private final Map<Type, CodecFactory> map;

		private MapJsonFactory(Map<Type, CodecFactory> map) {
			this.map = map;
		}

		public void addFactory(Type type, CodecFactory factory) {
			map.put(type, factory);
		}

		@Override
		public UniversalCodec<?> createCodec(Type type, CodecRegistry registry) {
			CodecFactory fac = map.get(type);
			return fac == null ? null : fac.createCodec(type, registry);
		}

		@Override
		public UniversalSerializer<?> createSerializer(Type type, CodecRegistry registry) {
			CodecFactory fac = map.get(type);
			return fac == null ? null : fac.createSerializer(type, registry);
		}

		@Override
		public UniversalDeserializer<?> createDeserializer(Type type, CodecRegistry registry) {
			CodecFactory fac = map.get(type);
			return fac == null ? null : fac.createDeserializer(type, registry);
		}
	}
}
