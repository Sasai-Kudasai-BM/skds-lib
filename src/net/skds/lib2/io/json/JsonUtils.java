package net.skds.lib2.io.json;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecFactory;
import net.skds.lib2.io.json.codec.JsonCodecOptions;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.elements.JsonElement;
import net.w3e.lib.utils.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@UtilityClass
public class JsonUtils {

	@Getter
	private static JsonCodecRegistry compactRegistry;
	@Getter
	private static JsonCodecRegistry fancyRegistry;
	private static JsonCodecOptions options;
	private static final JsonCodecFactory.MapJsonFactory userCodecFactory = JsonCodecFactory.newMapFactory();

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
		userCodecFactory.addFactory(type, factory);
		rebuild();
	}

	public static void addCodec(Type type, JsonCodec<?> codec) {
		userCodecFactory.addFactory(type, (t, r) -> codec);
		rebuild();
	}

	public static <T> T parseJson(String text, Class<T> clazz) {
		try {
			return fancyRegistry.getCodec(clazz).parse(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T parseJson(JsonElement json, Class<T> clazz) {
		try {
			return fancyRegistry.getCodec(clazz).parse(json);
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T readJson(InputStream is, Class<T> clazz) {
		try {
			String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return parseJson(text, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// FANCY
	@SuppressWarnings("unchecked")
	public static <T> String toJson(T object) {
		Class<T> type = (Class<T>) object.getClass();
		return fancyRegistry.getCodec(type).toJson(object);
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
			e.printStackTrace();
		}
		return false;
	}

	// COMPACT

	@SuppressWarnings("unchecked")
	public static <T> String toJsonCompact(T object) {
		Class<T> type = (Class<T>) object.getClass();
		return compactRegistry.getCodec(type).toJson(object);
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
			e.printStackTrace();
		}
		return false;
	}

	static {
		setOptions(new JsonCodecOptions());
	}
}
