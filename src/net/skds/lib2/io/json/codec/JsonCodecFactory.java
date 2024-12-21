package net.skds.lib2.io.json.codec;

import java.lang.reflect.Type;

@FunctionalInterface
public interface JsonCodecFactory {

	<T> JsonCodec<T> createCodec(Type type, JsonCodecRegistry registry);

}
