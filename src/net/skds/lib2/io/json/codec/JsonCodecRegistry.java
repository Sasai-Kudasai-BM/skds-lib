package net.skds.lib2.io.json.codec;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JsonCodecRegistry {

	private final Map<Type, JsonCodec<?>> codecMap = new ConcurrentHashMap<>();
	private final JsonCodecFactory codecFactory;
	private final Function<? super Type, ? extends JsonCodec<?>> mappingFunction;

	public JsonCodecRegistry(JsonCodecFactory codecFactory, Map<Type, JsonCodec<?>> basicCodecs) {
		this.codecFactory = codecFactory;
		this.mappingFunction = t -> codecFactory.createCodec(t, this);
		if (basicCodecs != null && !basicCodecs.isEmpty()) {
			codecMap.putAll(basicCodecs);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> JsonCodec<T> getCodec(Type type) {
		return (JsonCodec<T>) codecMap.computeIfAbsent(type, mappingFunction);
	}


	@SuppressWarnings("unchecked")
	public <T> JsonCodec<T> getCodec(Class<T> type) {
		return (JsonCodec<T>) codecMap.computeIfAbsent(type, mappingFunction);
	}
}
