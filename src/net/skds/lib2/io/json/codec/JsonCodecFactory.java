package net.skds.lib2.io.json.codec;

import java.lang.reflect.Type;

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
}
