package net.skds.lib2.io.json;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.skds.lib2.io.json.codec.*;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
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

	public static <AT, CT extends AT, E extends Enum<E> & ConfigType<CT>> void addTypedAdapter(Class<AT> type, Class<E> typeClass) {
		userCodecFactory.addFactory(type, (t, r) -> new AbstractJsonCodec<AT>(t, r) {

			@Override
			@SuppressWarnings("unchecked")
			public void write(AT value, JsonWriter writer) throws IOException {
				if (value == null) {
					writer.writeNull();
					return;
				}
				if (!(value instanceof TypedConfig tc)) {
					throw new UnsupportedOperationException("Value \"" + value + "\" is not a TypedConfig");
				}
				if (value instanceof JsonPreSerializeCall jps) {
					jps.preSerializeJson();
				}
				writer.beginObject();
				E type = (E) tc.getConfigType();
				writer.writeName(type.keyName());
				JsonSerializer<CT> serializer = this.registry.getSerializer(type.getTypeClass());
				serializer.write((CT) value, writer);
				writer.endObject();
			}

			@Override
			public AT read(JsonReader reader) throws IOException {
				if (reader.nextEntryType() == JsonEntryType.NULL) {
					reader.skipNull();
					return null;
				}
				reader.beginObject();
				String typeName = reader.readName();
				E type = Enum.valueOf(typeClass, typeName);
				JsonDeserializer<CT> deserializer = this.registry.getDeserializer(type.getTypeClass());
				CT value = deserializer.read(reader);
				reader.endObject();
				if (value instanceof JsonPostDeserializeCall jpi) {
					jpi.postDeserializedJson();
				}
				return value;
			}

		});
		rebuild();
	}

	public static <AT, CT extends AT> void addTypedAdapter(Class<AT> type, Map<String, ? extends ConfigType<?>> typeMap) {
		userCodecFactory.addFactory(type, (t, r) -> new AbstractJsonCodec<AT>(t, r) {

			@Override
			@SuppressWarnings("unchecked")
			public void write(AT value, JsonWriter writer) throws IOException {
				//System.out.println(((FormattedJsonWriterImpl)writer).getOutput());
				if (value == null) {
					writer.writeNull();
					return;
				}
				if (!(value instanceof TypedConfig tc)) {
					throw new UnsupportedOperationException("Value \"" + value + "\" is not a TypedConfig");
				}
				if (value instanceof JsonPreSerializeCall jps) {
					jps.preSerializeJson();
				}
				writer.beginObject();
				ConfigType<CT> type = (ConfigType<CT>) tc.getConfigType();
				writer.writeName(type.keyName());
				JsonSerializer<CT> serializer = this.registry.getSerializer(type.getTypeClass());
				serializer.write((CT) value, writer);
				writer.endObject();
			}

			@Override
			@SuppressWarnings("unchecked")
			public AT read(JsonReader reader) throws IOException {
				if (reader.nextEntryType() == JsonEntryType.NULL) {
					reader.skipNull();
					return null;
				}
				reader.beginObject();
				String typeName = reader.readName();
				ConfigType<CT> type = (ConfigType<CT>) typeMap.get(typeName);
				if (type == null) {
					throw new NullPointerException("type is null \"" + typeName + "\" " + typeMap.keySet());
				}
				JsonDeserializer<CT> deserializer = this.registry.getDeserializer(type.getTypeClass());
				CT value = deserializer.read(reader);
				reader.endObject();
				if (value instanceof JsonPostDeserializeCall jpi) {
					jpi.postDeserializedJson();
				}
				return value;
			}

		});
		rebuild();
	}

	public static <T> T parseJson(String text, Class<T> clazz) {
		try {
			return fancyRegistry.getDeserializer(clazz).parse(text);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	public static <T> T parseJson(JsonElement json, Class<T> clazz) {
		try {
			return fancyRegistry.getDeserializer(clazz).parse(json);
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

	// FANCY
	@SuppressWarnings("unchecked")
	public static <T> String toJson(T object) {
		Class<T> type = (Class<T>) object.getClass();
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
