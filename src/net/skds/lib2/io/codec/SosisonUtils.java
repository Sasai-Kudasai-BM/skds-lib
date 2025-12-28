package net.skds.lib2.io.codec;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.skds.lib2.annotations.NotNull;
import net.skds.lib2.io.codec.annotation.DefaultFile;
import net.skds.lib2.io.codec.annotation.EnumNameDataFixer;
import net.skds.lib2.io.codec.typed.ConfigEnumType;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.io.codec.typed.TypedEnumAdapter;
import net.skds.lib2.io.codec.typed.TypedMapAdapter;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.json.JsonCodecOptions;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.reflection.ReflectUtils;
import net.w3e.lib.utils.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.function.Supplier;

@UtilityClass
@SuppressWarnings("unused")
public class SosisonUtils {

	@Getter
	private static CodecRegistry compactRegistry;
	@Getter
	private static CodecRegistry fancyRegistry;
	@Getter
	private static CodecRegistry json5Registry;
	@Getter
	private static CodecRegistry jsonCRegistry;
	private static UniversalCodecOptions options;
	private static final CodecFactory.MapJsonFactory userMapCodecFactory = CodecFactory.newMapFactory();
	private static CodecFactory userCodecFactory = userMapCodecFactory;

	public static UniversalCodecOptions getOptions() {
		return options.clone();
	}

	public static void setOptions(UniversalCodecOptions options) {
		SosisonUtils.options = options.clone();
		rebuild();
	}

	private static void rebuild() {
		UniversalCodecOptions op = options.clone();
		compactRegistry = new CodecRegistry(op.setDecorationType(UniversalCodecOptions.DecorationType.FLAT), userCodecFactory);
		fancyRegistry = new CodecRegistry(op.setDecorationType(UniversalCodecOptions.DecorationType.FANCY), userCodecFactory);
		json5Registry = new CodecRegistry(op
				.setDecorationType(UniversalCodecOptions.DecorationType.FANCY)
				.setCapabilityVersion(JsonCodecOptions.JsonCapabilityVersion.JSON5),
				userCodecFactory);
		jsonCRegistry = new CodecRegistry(op
				.setDecorationType(UniversalCodecOptions.DecorationType.FANCY)
				.setCapabilityVersion(JsonCodecOptions.JsonCapabilityVersion.JSON_WITH_COMMENTS),
				userCodecFactory
		);
	}

	public static void addRedirectType(Type original, Type replaced) {
		addFactory(original, (t, r) -> new ReplacedCodec(t, replaced, r));
	}

	public static void addFactory(Type type, CodecFactory factory) {
		userMapCodecFactory.addFactory(type, factory);
		rebuild();
	}

	public static void addFactoryBefore(CodecFactory factory) {
		userCodecFactory = factory.orElse(userCodecFactory);
		rebuild();
	}

	public static void addFactoryAfter(CodecFactory factory) {
		userCodecFactory = userCodecFactory.orElse(factory);
		rebuild();
	}

	public static <CT, E extends Enum<E> & ConfigEnumType<? extends CT>> void addTypedAdapter(Class<CT> type, Class<E> typeClass) {
		userMapCodecFactory.addFactory(type, (t, r) -> new TypedEnumAdapter<>(t, typeClass, r));
		rebuild();
		//fancyRegistry.getCodec(type);
	}

	public static <CT> void addTypedAdapter(Class<CT> type, Map<String, ? extends ConfigType<?>> typeMap) {
		userMapCodecFactory.addFactory(type, (t, r) -> new TypedMapAdapter<>(t, typeMap, r));
		rebuild();
		//fancyRegistry.getCodec(type);
	}

	public static <T> T parseJson(String text, Class<T> type) {
		try {
			UniversalDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
			return deserializer.parse(text);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static <T> T parseJson(JsonElement json, Class<T> type) {
		try {
			UniversalDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
			return deserializer.parse(json);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static <E extends Enum<E>> E parseEnum(String value, Class<E> type) {
		return parseEnum(value, type, false);
	}

	// TODO enumNameDataFixer
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E parseEnum(String value, Class<E> type, boolean throwException) {
		try {
			try {
				return Enum.valueOf(type, value);
			} catch (Exception e) {
				EnumNameDataFixer annotation = type.getAnnotation(EnumNameDataFixer.class);
				if (annotation != null) {
					IEnumNameDataFixer<E> fixer = (IEnumNameDataFixer<E>) annotation.value().newInstance();
					return fixer.fixName(value);
				}
				throw e;
			}
		} catch (Exception e) {
			if (throwException)
				throw new IllegalStateException(e);
			return null;
		}
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
			new ParseException("Exception while reading " + file, e).printStackTrace(System.err);
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

	@SuppressWarnings("unchecked")
	public static <T> T readJson(T... type) {
		if (type.length != 0) throw new IllegalStateException("Are you Dolbaeb?!");
		Class<T> tClass = (Class<T>) type.getClass().getComponentType();
		return readJson(tClass);
	}

	public static <T> T readJson(@NotNull Class<T> tClass) {
		DefaultFile df = tClass.getAnnotation(DefaultFile.class);
		if (df == null) {
			throw new IllegalArgumentException("Class \"" + tClass.getSimpleName() + "\" is not annotated with @DefaultFile");
		}
		return readJson(Path.of(df.value()), tClass);
	}

	@NotNull
	@SuppressWarnings("unchecked")
	public static <T> T readOrCreateJson(T... type) {
		if (type.length != 0) throw new IllegalStateException("Are you Dolbaeb?!");
		Class<T> tClass = (Class<T>) type.getClass().getComponentType();
		return readOrCreateJson(tClass);
	}

	@NotNull
	public static <T> T readOrCreateJson(@NotNull Class<T> tClass) {
		DefaultFile df = tClass.getAnnotation(DefaultFile.class);
		if (df == null) {
			throw new IllegalArgumentException("Class \"" + tClass.getSimpleName() + "\" is not annotated with @DefaultFile");
		}
		return readOrCreateJson(Path.of(df.value()), tClass);
	}

	@NotNull
	public static <T> T readOrCreateJson(@NotNull Path file, @NotNull Class<T> tClass) {
		try {
			if (Files.exists(file)) {
				String text = Files.readString(file);
				return parseJson(text, tClass);
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		Supplier<T> constructor = ReflectUtils.getConstructor(tClass);
		if (constructor != null) {
			T obj = constructor.get();
			saveJson(file, obj);
			return obj;
		} else {
			throw new RuntimeException("No constructor for class \"" + tClass.getSimpleName() + "\"");
		}
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
			UniversalDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
			return deserializer.parse(text);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}


	public static <T> T parseJson(JsonElement json, Type type) {
		try {
			UniversalDeserializer<T> deserializer = fancyRegistry.getDeserializer(type);
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
		Class<Object> type = (Class<Object>) object.getClass();
		return fancyRegistry.getSerializer(type).toJson(object);
	}

	public static boolean saveJson(Object cfg) {
		Class<?> tClass = cfg.getClass();
		DefaultFile df = tClass.getAnnotation(DefaultFile.class);
		if (df == null) {
			throw new IllegalArgumentException("Class \"" + tClass.getSimpleName() + "\" is not annotated with @DefaultFile");
		}
		return saveJson(df.value(), cfg);
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
		setOptions(new UniversalCodecOptionsImpl());
	}
}
