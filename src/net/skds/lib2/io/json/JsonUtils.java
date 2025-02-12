package net.skds.lib2.io.json;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.skds.lib2.io.json.codec.*;
import net.skds.lib2.io.json.codec.typed.ConfigEnumType;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.io.json.codec.typed.TypedEnumAdapter;
import net.skds.lib2.io.json.codec.typed.TypedMapAdapter;
import net.skds.lib2.io.json.elements.JsonElement;
import net.w3e.lib.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@UtilityClass
@SuppressWarnings("unused")
public class JsonUtils {

	@Getter
	private static JsonCodecRegistry compactRegistry;
	@Getter
	private static JsonCodecRegistry fancyRegistry;
	private static JsonCodecOptions options;
	private static final JsonCodecFactory.MapJsonFactory userMapCodecFactory = JsonCodecFactory.newMapFactory();
	private static JsonCodecFactory userCodecFactory = userMapCodecFactory;

	public static JsonCodecOptions getOptions() {
		return options.clone();
	}

	public static void setOptions(JsonCodecOptions options) {
		JsonUtils.options = options.clone();
		rebuild();
	}

	private static void rebuild() {
		JsonCodecOptions op = options.clone();
		compactRegistry = new JsonCodecRegistry(op.setDecorationType(JsonCodecOptions.DecorationType.FLAT), userCodecFactory);
		fancyRegistry = new JsonCodecRegistry(op.setDecorationType(JsonCodecOptions.DecorationType.FANCY), userCodecFactory);
	}

	public static void addFactory(Type type, JsonCodecFactory factory) {
		userMapCodecFactory.addFactory(type, factory);
		rebuild();
	}

	public static void addFactoryBefore(JsonCodecFactory factory) {
		userCodecFactory = factory.orElse(userCodecFactory);
		rebuild();
	}

	public static void addFactoryAfter(JsonCodecFactory factory) {
		userCodecFactory = userCodecFactory.orElse(factory);
		rebuild();
	}

	public static <CT, E extends Enum<E> & ConfigEnumType<CT>> void addTypedAdapter(Class<CT> type, Class<E> typeClass) {
		userMapCodecFactory.addFactory(type, (t, r) -> new TypedEnumAdapter<>(t, typeClass, r));
		//fancyRegistry.getCodec(type);
		rebuild();
	}

	public static <CT> void addTypedAdapter(Class<CT> type, Map<String, ? extends ConfigType<?>> typeMap) {
		userMapCodecFactory.addFactory(type, (t, r) -> new TypedMapAdapter<>(t, typeMap, r));
		//fancyRegistry.getCodec(type);
		rebuild();
	}

	public static <T> T parseJson(String text, Class<T> type) {
		try {
			JsonDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
			return deserializer.parse(text);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}


	public static <T> T parseJson(JsonElement json, Class<T> type) {
		try {
			JsonDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
			return deserializer.parse(json);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static <T> T readJson(String file, Class<T> clazz) {
		return readJson(Path.of(file), clazz);
	}

	public static <T> T readJson(File file, Class<T> clazz) {
		return readJson(file.toPath(), clazz);
	}

	public static <T> T readJson(Path file, Class<T> clazz) {
		try {
			String text = Files.readString(file);
			return parseJson(text, clazz);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static <T> T readJson(InputStream is, Class<T> clazz) {
		try {
			String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return parseJson(text, clazz);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}


	public static <T> T readJson(String file, Type type) {
		return readJson(Path.of(file), type);
	}

	public static <T> T readJson(File file, Type type) {
		return readJson(file.toPath(), type);
	}

	public static <T> T readJson(Path file, Type type) {
		try {
			String text = Files.readString(file);
			return parseJson(text, type);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static <T> T readJson(InputStream is, Type type) {
		try {
			String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return parseJson(text, type);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static <T> T parseJson(String text, Type type) {
		try {
			JsonDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
			return deserializer.parse(text);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}


	public static <T> T parseJson(JsonElement json, Type type) {
		try {
			JsonDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
			return deserializer.parse(json);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	// FANCY
	@SuppressWarnings("unchecked")
	public static String toJson(Object object) {
		if (object == null) {
			return "null";
		}
		Class<Object> type = (Class<Object>)object.getClass();
		return fancyRegistry.getSerializer(type).toJson(object);
	}


	public static boolean saveJson(String path, Object cfg) {
		return saveJson(Path.of(path), cfg);
	}

	public static boolean saveJson(File path, Object cfg) {
		return saveJson(path.toPath(), cfg);
	}

	public static boolean saveJson(Path path, Object cfg) {
		try {
			String text = toJson(cfg);
			FileUtils.createParentDirs(path);
			Files.writeString(path, text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return false;
	}

	// COMPACT

	@SuppressWarnings("unchecked")
	public static <T> String toJsonCompact(T object) {
		if (object == null) {
			return "null";
		}
		Class<T> type = (Class<T>) object.getClass();
		return compactRegistry.getSerializer(type).toJson(object);
	}

	public static boolean saveJsonCompact(String path, Object cfg) {
		return saveJsonCompact(Path.of(path), cfg);
	}

	public static boolean saveJsonCompact(File path, Object cfg) {
		return saveJsonCompact(path.toPath(), cfg);
	}

	public static boolean saveJsonCompact(Path path, Object cfg) {
		try {
			String text = toJsonCompact(cfg);
			FileUtils.createParentDirs(path);
			Files.writeString(path, text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
			return true;
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return false;
	}

	static {
		setOptions(new JsonCodecOptions());
	}
}
