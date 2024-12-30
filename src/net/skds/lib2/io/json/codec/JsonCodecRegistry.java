package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.CharOutput;
import net.skds.lib2.io.json.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JsonCodecRegistry {

	private final JsonCodecOptions options;
	private final Map<Type, JsonCodec<?>> codecMap = new ConcurrentHashMap<>();
	private final Function<? super Type, ? extends JsonCodec<?>> mappingFunction;
	private static final JsonCodecFactory builtin = new BuiltinCodecFactory();

	public JsonCodecRegistry(JsonCodecOptions options, JsonCodecFactory extraFactory) {
		this.options = options.clone();
		JsonCodecFactory combined;
		if (extraFactory != null) {
			combined = builtin.orElse(extraFactory);
		} else {
			combined = builtin;
		}
		// TODO orElse reflective
		this.mappingFunction = t -> combined.createCodec(t, this);
	}

	public JsonReader createReader(CharInput input) {
		return new JsonReaderImpl(input);
	}

	public JsonWriter createWriter(CharOutput output) {
		return switch (options.getDecorationType()) {
			case FANCY -> new FormattedJsonWriterImpl(output, options.getTabulation());
			default -> new FlatJsonWriterImpl(output);
		};
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
