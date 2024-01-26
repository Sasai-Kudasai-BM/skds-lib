package net.skds.lib.utils.json;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import net.skds.lib.mat.Vec3;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CFGLoader {

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
						return new Vec3(j.get(0).getAsDouble(), j.get(1).getAsDouble(), j.get(2).getAsDouble());
					}
					if (peek == JsonToken.BEGIN_OBJECT) {
						JsonObject j = JO_ADAPTER.read(in);
						return new Vec3(j.get("x").getAsDouble(), j.get("y").getAsDouble(), j.get("z").getAsDouble());
					}
					return new Vec3();
				}

				@Override
				public void write(JsonWriter out, Vec3 value) throws IOException {
					if (value == null) {
						out.nullValue();
						return;
					}
					out.beginArray();
					out.value(value.x);
					out.value(value.y);
					out.value(value.z);
					out.endArray();
				}

			})
			.registerTypeAdapter(String.class, new TypeAdapter<String>() {

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

			});

	@Getter
	private static Gson GSON_COMPACT = builder.create();
	@Getter
	private static Gson GSON = GSON_COMPACT.newBuilder().setPrettyPrinting().create();
	private static final TypeAdapter<JsonElement> JE_ADAPTER = GSON.getAdapter(JsonElement.class);
	private static final TypeAdapter<JsonArray> JA_ADAPTER = GSON.getAdapter(JsonArray.class);
	private static final TypeAdapter<JsonObject> JO_ADAPTER = GSON.getAdapter(JsonObject.class);

	public static void addAdapter(Type type, TypeAdapter<?> adapter) {
		GSON_COMPACT = builder.registerTypeAdapter(type, adapter).create();
		GSON = GSON_COMPACT.newBuilder().setPrettyPrinting().create();
	}

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
				if (value instanceof JsonPostInit jpi) {
					jpi.jsomPostInit();
				}
				in.endObject();
				return value;
			}

			@Override
			public void write(JsonWriter out, CT value) throws IOException {
				throw new UnsupportedOperationException("TODO");
			}

		}).create();
		GSON = GSON_COMPACT.newBuilder().setPrettyPrinting().create();
	}

	public static <T> T readConfig(File file, Class<T> clazz) {
		return readConfig(file.toPath(), clazz);
	}

	public static <T> T parseConfig(String text, Class<T> clazz) {
		try {
			T cfg = GSON.fromJson(text, clazz);
			if (cfg instanceof JsonPostInit post) {
				post.jsomPostInit();
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

	public static <T> T readConfig(String file, Class<T> clazz) {
		return readConfig(Path.of(file), clazz);
	}

	private static final byte[] emptyBytes = {};

	public static byte[] readBytes(File file) {
		try {
			return Files.readAllBytes(file.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emptyBytes;
	}

	public static String readText(File file) {
		try {
			return Files.readString(file.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String toJsonCompact(Object cfg) {
		return GSON_COMPACT.toJson(cfg);
	}

	public static String toJson(Object cfg) {
		return GSON.toJson(cfg);
	}

	public static boolean saveConfig(File file, Object cfg) {
		try {
			String text = GSON.toJson(cfg);
			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}
			Files.writeString(file.toPath(), text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean saveConfigCompact(File file, Object cfg) {
		try {
			String text = GSON_COMPACT.toJson(cfg);
			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}
			Files.writeString(file.toPath(), text, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
