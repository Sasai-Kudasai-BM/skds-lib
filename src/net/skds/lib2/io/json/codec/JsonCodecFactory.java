package net.skds.lib2.io.json.codec;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface JsonCodecFactory {

	JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry);

	default JsonSerializer<?> createSerializer(Type type, JsonCodecRegistry registry) {
		return createCodec(type, registry);
	}

	default JsonDeserializer<?> createDeserializer(Type type, JsonCodecRegistry registry) {
		return createCodec(type, registry);
	}

	default JsonCodecFactory orElse(JsonCodecFactory other) {
		return new JsonCodecFactory() {
			@Override
			public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
				JsonCodec<?> codec = JsonCodecFactory.this.createCodec(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createCodec(type, registry);
			}

			@Override
			public JsonSerializer<?> createSerializer(Type type, JsonCodecRegistry registry) {
				JsonSerializer<?> codec = JsonCodecFactory.this.createSerializer(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createSerializer(type, registry);
			}

			@Override
			public JsonDeserializer<?> createDeserializer(Type type, JsonCodecRegistry registry) {
				JsonDeserializer<?> codec = JsonCodecFactory.this.createDeserializer(type, registry);
				if (codec != null) {
					return codec;
				}
				return other.createDeserializer(type, registry);
			}
		};
	}

	static MapJsonFactory newMapFactory() {
		return new MapJsonFactory(new ConcurrentHashMap<>());
	}

	final class MapJsonFactory implements JsonCodecFactory {

		private final Map<Type, JsonCodecFactory> map;

		private MapJsonFactory(Map<Type, JsonCodecFactory> map) {
			this.map = map;
		}

		public void addFactory(Type type, JsonCodecFactory factory) {
			map.put(type, factory);
		}

		@Override
		public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
			JsonCodecFactory fac = map.get(type);
			return fac == null ? null : fac.createCodec(type, registry);
		}

		@Override
		public JsonSerializer<?> createSerializer(Type type, JsonCodecRegistry registry) {
			JsonCodecFactory fac = map.get(type);
			return fac == null ? null : fac.createSerializer(type, registry);
		}

		@Override
		public JsonDeserializer<?> createDeserializer(Type type, JsonCodecRegistry registry) {
			JsonCodecFactory fac = map.get(type);
			return fac == null ? null : fac.createDeserializer(type, registry);
		}
	}
}
