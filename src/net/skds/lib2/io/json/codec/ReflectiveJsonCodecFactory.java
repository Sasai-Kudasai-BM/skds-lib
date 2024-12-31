package net.skds.lib2.io.json.codec;

import lombok.CustomLog;
import net.skds.lib2.io.json.*;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.reflection.ReflectUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@CustomLog
public class ReflectiveJsonCodecFactory implements JsonCodecFactory {

	private static final FieldCodec[] fieldCodecArray = {};

	@Override
	public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
		if (type instanceof Class<?> c) {
			checkForInnerClass(c);
			return new ReflectiveCodec(c, registry);
		} else if (type instanceof ParameterizedType pt) {
			if (pt.getRawType() instanceof Class<?> c) {
				checkForInnerClass(c);
			}
			return createCodec(pt.getRawType(), registry);
		}
		return null;
	}

	private static void checkForInnerClass(Class<?> c) {
		if (c.isMemberClass() && !Modifier.isStatic(c.getModifiers())) {
			throw new IllegalArgumentException("Can not create ReflectiveCodec for non-static inner class \"" + c + "\"");
		}
	}

	public static class ReflectiveCodec extends JsonCodec<Object> {

		final Supplier<Object> constructor;
		final FieldCodec[] writers;
		final Map<String, FieldCodec> readers;

		@SuppressWarnings("unchecked")
		public ReflectiveCodec(Class<?> tClass, JsonCodecRegistry registry) {
			super(registry);
			Supplier<Object> tmpC;
			if (tClass.isInterface() || Modifier.isAbstract(tClass.getModifiers())) {
				throw new IllegalArgumentException("Class \"" + tClass.getName() + "\" is abstract type and can not be created by ReflectiveCodec");
			} else {
				tmpC = ReflectUtils.getConstructor((Class<Object>) tClass);
				if (tmpC == null) {
					throw new IllegalArgumentException("Class \"" +
							tClass.getName() +
							"\" have no empty constructor and can not be created by ReflectiveCodec");
				}
			}
			this.constructor = tmpC;
			FieldCodec[] codecs = collectFields(tClass, registry);
			Map<String, FieldCodec> readers = new HashMap<>();
			for (int i = 0; i < codecs.length; i++) {
				FieldCodec fc = codecs[i];
				readers.put(fc.name, fc);
			}
			this.writers = codecs;
			this.readers = readers;
		}

		@Override
		public void write(Object value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			if (value instanceof JsonPreSerializeCall psc) {
				psc.preSerializeJson();
			}
			writer.beginObject();
			if (writers.length > 1) {
				writer.lineBreakEnable(true);
			}
			for (FieldCodec w : writers) {
				try {
					writer.writeName(w.name);
					w.write(writer, value);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			writer.endObject();
		}

		@Override
		public Object read(JsonReader reader) throws IOException {
			if (reader.nextEntryType() == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			Object o = constructor.get();
			reader.beginObject();
			while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
				String name = reader.readName();
				FieldCodec fc = readers.get(name);
				if (fc == null) continue;
				try {
					fc.read(reader, o);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
			reader.endObject();
			if (o instanceof JsonPostDeserializeCall pdc) {
				pdc.postDeserializedJson();
			}
			return o;
		}
	}

	private static FieldCodec[] collectFields(Class<?> c, JsonCodecRegistry registry) {
		ArrayList<FieldCodec> list = new ArrayList<>();
		collectFields(c, registry, list);
		return list.toArray(fieldCodecArray);
	}

	private static void collectFields(Class<?> c, JsonCodecRegistry registry, List<FieldCodec> list) {
		if (c == Object.class) return;

		JsonCodecOptions options = registry.options;
		for (Field f : c.getDeclaredFields()) {
			if ((f.getModifiers() & options.getExcludeFieldModifiers()) != 0) {
				continue;
			}
			Class<?> ct = f.getType();
			if (ct.isPrimitive()) {
				if (ct == int.class) {
					list.add(new IntFieldCodec(f));
				} else if (ct == float.class) {
					list.add(new FloatFieldCodec(f));
				} else if (ct == double.class) {
					list.add(new DoubleFieldCodec(f));
				} else if (ct == boolean.class) {
					list.add(new BooleanFieldCodec(f));
				} else if (ct == long.class) {
					list.add(new LongFieldCodec(f));
				} else if (ct == byte.class) {
					list.add(new ByteFieldCodec(f));
				} else if (ct == short.class) {
					list.add(new ShortFieldCodec(f));
				} else if (ct == char.class) {
					list.add(new CharFieldCodec(f, registry));
				}
			} else {
				ObjFieldCodec codec = new ObjFieldCodec(f, registry);
				list.add(codec);
			}
		}

		Class<?> sup = c.getSuperclass();
		if (sup != Object.class) {
			collectFields(sup, registry, list);
		}
	}

	private static abstract class FieldCodec {
		protected final Field field;
		protected final String name;

		private FieldCodec(Field field) {
			this.field = field;
			field.setAccessible(true);
			JsonAlias alias = field.getAnnotation(JsonAlias.class);
			String n;
			if (alias != null) {
				n = alias.value();
			} else {
				n = field.getName();
			}
			this.name = n;
		}

		abstract void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException;

		abstract void read(JsonReader reader, Object o) throws IOException, IllegalAccessException;
	}

	private static class ByteFieldCodec extends FieldCodec {

		private ByteFieldCodec(Field field) {
			super(field);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeInt(field.getByte(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setByte(o, reader.readNumber().byteValue());
		}
	}

	private static class ShortFieldCodec extends FieldCodec {

		private ShortFieldCodec(Field field) {
			super(field);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeInt(field.getShort(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setShort(o, reader.readNumber().shortValue());
		}
	}

	private static class IntFieldCodec extends FieldCodec {

		private IntFieldCodec(Field field) {
			super(field);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeInt(field.getInt(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setInt(o, reader.readNumber().intValue());
		}
	}

	private static class LongFieldCodec extends FieldCodec {

		private LongFieldCodec(Field field) {
			super(field);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeInt(field.getLong(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setLong(o, reader.readNumber().longValue());
		}
	}

	private static class FloatFieldCodec extends FieldCodec {

		private FloatFieldCodec(Field field) {
			super(field);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeFloat(field.getFloat(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setFloat(o, reader.readNumber().floatValue());
		}
	}

	private static class DoubleFieldCodec extends FieldCodec {

		private DoubleFieldCodec(Field field) {
			super(field);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeFloat(field.getDouble(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setDouble(o, reader.readNumber().doubleValue());
		}
	}

	private static class CharFieldCodec extends FieldCodec {
		final JsonCodec<Character> codec;

		private CharFieldCodec(Field field, JsonCodecRegistry registry) {
			super(field);
			this.codec = registry.getCodecIndirect(char.class);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeInt(field.getChar(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setChar(o, codec.read(reader));
		}
	}

	private static class BooleanFieldCodec extends FieldCodec {

		private BooleanFieldCodec(Field field) {
			super(field);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeBoolean(field.getBoolean(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setBoolean(o, reader.readBoolean());
		}
	}

	private static class ObjFieldCodec extends FieldCodec {

		final JsonCodec<Object> codec;

		private ObjFieldCodec(Field field, JsonCodecRegistry registry) {
			super(field);
			JsonCodec<Object> c = getDefaultCodec(field, registry);
			if (c == null) {
				c = registry.getCodecIndirect(field.getGenericType());
			}
			this.codec = c;
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			codec.write(field.get(o), writer);
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.set(o, codec.read(reader));
		}
	}

	@SuppressWarnings("unchecked")
	private static JsonCodec<Object> getDefaultCodec(Field field, JsonCodecRegistry registry) {
		DefaultJsonCodec defaultCodec = field.getAnnotation(DefaultJsonCodec.class);
		if (defaultCodec == null) return null;
		Supplier<JsonCodecFactory> constructor = (Supplier<JsonCodecFactory>) ReflectUtils.getConstructor(defaultCodec.value());
		if (constructor != null) {
			JsonCodecFactory factory = constructor.get();
			if (factory != null) {
				JsonCodec<Object> codec = (JsonCodec<Object>) factory.createCodec(field.getGenericType(), registry);
				if (codec != null) return codec;
			}
		}

		log.error("Invalid @DefaultJsonCodec on \"" + field + "\"");
		return null;
	}

}
