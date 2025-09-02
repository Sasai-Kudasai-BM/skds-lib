package net.skds.lib2.io.codec;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface UniversalCodecFactory {

	UniversalCodec<?> createCodec(Type type, UniversalCodecRegistry registry);

	default UniversalSerializer<?> createSerializer(Type type, UniversalCodecRegistry registry) {
		return createCodec(type, registry);
	}

	default UniversalDeserializer<?> createDeserializer(Type type, UniversalCodecRegistry registry) {
		return createCodec(type, registry);
	}

	default UniversalCodecFactory orElse(UniversalCodecFactory other) {
		return new UniversalCodecFactory() {
			@Override
			public UniversalCodec<?> createCodec(Type type, UniversalCodecRegistry registry) {
				UniversalCodec<?> codec = UniversalCodecFactory.this.createCodec(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createCodec(type, registry);
			}

			@Override
			public UniversalSerializer<?> createSerializer(Type type, UniversalCodecRegistry registry) {
				UniversalSerializer<?> codec = UniversalCodecFactory.this.createSerializer(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createSerializer(type, registry);
			}

			@Override
			public UniversalDeserializer<?> createDeserializer(Type type, UniversalCodecRegistry registry) {
				UniversalDeserializer<?> codec = UniversalCodecFactory.this.createDeserializer(type, registry);
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

	public final class MapJsonFactory implements UniversalCodecFactory {

		private final Map<Type, UniversalCodecFactory> map;

		private MapJsonFactory(Map<Type, UniversalCodecFactory> map) {
			this.map = map;
		}

		public void addFactory(Type type, UniversalCodecFactory factory) {
			map.put(type, factory);
		}

		@Override
		public UniversalCodec<?> createCodec(Type type, UniversalCodecRegistry registry) {
			UniversalCodecFactory fac = map.get(type);
			return fac == null ? null : fac.createCodec(type, registry);
		}

		@Override
		public UniversalSerializer<?> createSerializer(Type type, UniversalCodecRegistry registry) {
			UniversalCodecFactory fac = map.get(type);
			return fac == null ? null : fac.createSerializer(type, registry);
		}

		@Override
		public UniversalDeserializer<?> createDeserializer(Type type, UniversalCodecRegistry registry) {
			UniversalCodecFactory fac = map.get(type);
			return fac == null ? null : fac.createDeserializer(type, registry);
		}
	}
}
