package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.CodecRole;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FunctionalInterface
public interface JsonCodecFactory {

	JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry);

	default JsonCodecFactory combine(JsonCodecFactory other) {
		if (getCodecRole() == other.getCodecRole()) return other;
		return switch (other.getCodecRole()) {
			case BOTH -> other;

			case SERIALIZE ->
					(t, r) -> new CombinedJsonCodec<>(other.createCodec(t, r), JsonCodecFactory.this.createCodec(t, r));

			case DESERIALIZE ->
					(t, r) -> new CombinedJsonCodec<>(JsonCodecFactory.this.createCodec(t, r), other.createCodec(t, r));

		};
	}

	default CodecRole getCodecRole() {
		return CodecRole.BOTH;
	}

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
			map.compute(type, (t, f) -> {
				if (f == null) {
					return factory;
				}
				return f.combine(factory);
			});
		}

		@Override
		public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
			JsonCodecFactory fac = map.get(type);
			return fac == null ? null : fac.createCodec(type, registry);
		}
	}
}
