package net.skds.lib2.io.json.codec;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface JsonCodecFactory {

	JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry);

	default JsonCodecFactory orElse(JsonCodecFactory other) {
		return (t, r) -> {
			JsonCodec<?> c = JsonCodecFactory.this.createCodec(t, r);
			if (c == null) {
				c = other.createCodec(t, r);
			}
			return c;
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
	}
}
