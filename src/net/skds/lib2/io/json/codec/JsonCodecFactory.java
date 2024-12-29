package net.skds.lib2.io.json.codec;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

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

	class MapJsonFactory implements JsonCodecFactory {

		private final Map<Type, Function<JsonCodecRegistry, JsonCodec<?>>> map;

		public MapJsonFactory(Map<Type, Function<JsonCodecRegistry, JsonCodec<?>>> map) {
			this.map = map;
		}

		@Override
		public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
			Function<JsonCodecRegistry, JsonCodec<?>> fac = map.get(type);
			return fac == null ? null : fac.apply(registry);
		}
	}
}
