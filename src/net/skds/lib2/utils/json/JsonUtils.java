package net.skds.lib2.utils.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import net.sdteam.libmerge.Lib1Merge;
import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonPreSerializeCall;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.mat.Vec3;
import net.skds.lib2.mat.Vec3D;
import net.w3e.lib.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@Deprecated
public class JsonUtils {

	private static final GsonBuilder builder = new GsonBuilder()
			.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
			.registerTypeAdapter(Vec3.class, new TypeAdapter<Vec3>() {

				@Override
				public Vec3 read(JsonReader in) throws IOException {
					JsonToken peek = in.peek();
					if (peek == JsonToken.NULL) {
						in.nextNull();
						return null;
					}
					if (peek == JsonToken.BEGIN_ARRAY) {
						JsonArray j = JA_ADAPTER.read(in);
						return new Vec3D(j.get(0).getAsDouble(), j.get(1).getAsDouble(), j.get(2).getAsDouble());
					}
					if (peek == JsonToken.BEGIN_OBJECT) {
						JsonObject j = JO_ADAPTER.read(in);
						return new Vec3D(j.get("xf").getAsDouble(), j.get("yf").getAsDouble(), j.get("zf").getAsDouble());
					}
					return Vec3.ZERO;
				}

				@Override
				public void write(JsonWriter out, Vec3 value) throws IOException {
					if (value == null) {
						out.nullValue();
						return;
					}
					out.beginArray();
					out.value(value.x());
					out.value(value.y());
					out.value(value.z());
					out.endArray();
				}

			}).registerTypeAdapter(String.class, new TypeAdapter<String>() {

				@Override
				public String read(JsonReader in) throws IOException {
					JsonToken peek = in.peek();
					if (peek == JsonToken.NULL) {
						in.nextNull();
						return null;
					}
					if (peek == JsonToken.BEGIN_OBJECT || peek == JsonToken.BEGIN_ARRAY) {
						JsonElement element = JE_ADAPTER.read(in);
						return element.toString();
					}
					if (peek == JsonToken.BOOLEAN) {
						return Boolean.toString(in.nextBoolean());
					}
					return in.nextString();
				}

				@Override
				public void write(JsonWriter out, String value) throws IOException {
					out.value(value);
				}

			}).registerTypeAdapter(File.class, new TypeAdapter<File>() {

				@Override
				public File read(JsonReader in) throws IOException {
					return new File(in.nextString());
				}

				@Override
				public void write(JsonWriter out, File value) throws IOException {
					out.value(value.getName());
				}

			});
	@Getter
	private static Gson GSON_COMPACT = builder.create();
	@Getter
	@Lib1Merge
	private static Gson GSON_NULL_COMPACT;
	@Getter
	private static Gson GSON;

	static {
		updateGson();
	}

	private static final TypeAdapter<JsonElement> JE_ADAPTER = GSON.getAdapter(JsonElement.class);
	private static final TypeAdapter<JsonArray> JA_ADAPTER = GSON.getAdapter(JsonArray.class);
	private static final TypeAdapter<JsonObject> JO_ADAPTER = GSON.getAdapter(JsonObject.class);

	@Lib1Merge
	public static void addAdapter(Type type, TypeAdapter<?> adapter) {
		GSON_COMPACT = builder.registerTypeAdapter(type, adapter).create();
		updateGson();
	}

	@Lib1Merge
	public static void addAdapter(Type type, Object adapter) {
		GSON_COMPACT = builder.registerTypeAdapter(type, adapter).create();
		updateGson();
	}

	@Lib1Merge
	public static <AT, CT extends AT, E extends Enum<E> & ConfigType<CT>> void addTypedAdapter(Class<AT> type, Class<E> typeClass) {
		GSON_COMPACT = builder.registerTypeAdapter(type, new TypeAdapter<CT>() {

			@Override
			public CT read(JsonReader in) throws IOException {
				JsonToken peek = in.peek();
				if (peek == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				in.beginObject();
				String typeName = in.nextName();
				E type = Enum.valueOf(typeClass, typeName);

				TypeAdapter<CT> adapter = GSON.getAdapter(type.getTypeClass());
				CT value = adapter.read(in);
				if (value instanceof JsonPostDeserializeCall jpi) {
					jpi.postDeserializedJson();
				}
				in.endObject();
				return value;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void write(JsonWriter out, CT value) throws IOException {
				if (!(value instanceof TypedConfig tc)) {
					throw new UnsupportedOperationException("Value is not a TypedConfig");
				}
				out.beginObject();
				E type = (E) tc.getConfigType();
				out.name(type.keyName());
				TypeAdapter<CT> adapter = GSON.getAdapter(type.getTypeClass());
				if (value instanceof JsonPreSerializeCall jps) {
					jps.preSerializeJson();
				}
				adapter.write(out, value);
				out.endObject();
			}

		}).create();
		updateGson();
	}

	@Lib1Merge
	public static <AT, CT extends AT> void addTypedAdapter(Class<AT> type, Map<String, ? extends ConfigType<?>> typeMap) {
		GSON_COMPACT = builder.registerTypeAdapter(type, new TypeAdapter<CT>() {

			@SuppressWarnings("unchecked")
			@Override
			public CT read(JsonReader in) throws IOException {
				JsonToken peek = in.peek();
				if (peek == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				in.beginObject();
				String typeName = in.nextName();
				ConfigType<CT> type = (ConfigType<CT>) typeMap.get(typeName);
				if (type == null) {
					throw new NullPointerException("type is null \"" + typeName + "\" " + typeMap.keySet());
				}

				TypeAdapter<CT> adapter = GSON.getAdapter(type.getTypeClass());
				CT value = adapter.read(in);
				if (value instanceof JsonPostDeserializeCall jpi) {
					jpi.postDeserializedJson();
				}
				in.endObject();
				return value;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void write(JsonWriter out, CT value) throws IOException {
				if (!(value instanceof TypedConfig tc)) {
					throw new UnsupportedOperationException("Value is not a TypedConfig");
				}
				out.beginObject();
				ConfigType<CT> type = (ConfigType<CT>) tc.getConfigType();
				out.name(type.keyName());
				TypeAdapter<CT> adapter = GSON.getAdapter(type.getTypeClass());
				if (value instanceof JsonPreSerializeCall jps) {
					jps.preSerializeJson();
				}
				adapter.write(out, value);
				out.endObject();
			}

		}).create();
		updateGson();
	}

	@Lib1Merge
	private static void updateGson() {
		GSON = GSON_COMPACT.newBuilder().setPrettyPrinting().create();
		GSON_NULL_COMPACT = GSON_COMPACT.newBuilder().serializeNulls().create();
	}

	public static <T> T readConfig(File file, Class<T> clazz) {
		return readConfig(file.toPath(), clazz);
	}

	public static <T> T parseConfig(String text, Class<T> clazz) {
		try {
			T cfg = GSON.fromJson(text, clazz);
			if (cfg instanceof JsonPostDeserializeCall post) {
				post.postDeserializedJson();
			}
			return cfg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T parseConfig(JsonElement json, Class<T> clazz) {
		try {
			T cfg = GSON.fromJson(json, clazz);
			if (cfg instanceof JsonPostDeserializeCall post) {
				post.postDeserializedJson();
			}
			return cfg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T readConfig(Path file, Class<T> clazz) {
		try {
			String text = Files.readString(file);
			return parseConfig(text, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T readConfig(InputStream is, Class<T> clazz) {
		try {
			String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			return parseConfig(text, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T readConfig(String file, Class<T> clazz) {
		return readConfig(Path.of(file), clazz);
	}

	public static String toJsonCompact(Object cfg) {
		if (cfg instanceof JsonPreSerializeCall pre) {
			pre.preSerializeJson();
		}
		return GSON_COMPACT.toJson(cfg);
	}

	@Lib1Merge
	public static String toJsonCompactNull(Object cfg) {
		if (cfg instanceof JsonPreSerializeCall pre) {
			pre.preSerializeJson();
		}
		return GSON_NULL_COMPACT.toJson(cfg);
	}

	public static String toJson(Object cfg) {
		if (cfg instanceof JsonPreSerializeCall pre) {
			pre.preSerializeJson();
		}
		return GSON.toJson(cfg);
	}

	public static boolean saveConfig(String file, Object cfg) {
		return saveConfig(new File(file), cfg);
	}

	public static boolean saveConfig(File file, Object cfg) {
		try {
			String text = toJson(cfg);
			FileUtils.createParentDirs(file);
			Files.writeString(file.toPath(), text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean saveConfigCompact(File file, Object cfg) {
		try {
			String text = toJsonCompact(cfg);
			FileUtils.createParentDirs(file);
			Files.writeString(file.toPath(), text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
