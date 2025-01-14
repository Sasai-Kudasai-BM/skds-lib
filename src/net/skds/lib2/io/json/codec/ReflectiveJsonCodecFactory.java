package net.skds.lib2.io.json.codec;

import lombok.CustomLog;
import net.skds.lib2.io.json.*;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.JsonCodecRoleConstrains;
import net.skds.lib2.io.json.annotation.TransientComponent;
import net.skds.lib2.reflection.ReflectUtils;
import net.skds.lib2.utils.Numbers;
import net.skds.lib2.utils.function.MultiSupplier;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@CustomLog
public class ReflectiveJsonCodecFactory implements JsonCodecFactory {

	private static final FieldCodec[] fieldCodecArray = {};

	public static final ReflectiveJsonCodecFactory INSTANCE = new ReflectiveJsonCodecFactory();

	@Override
	public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
		if (type instanceof Class<?> c) {
			if (c.isInterface()) {
				return null;
			}
			checkForInnerClass(c);
			if (c.isRecord()) {
				return getRecordCodec(c, registry);
			}
			return getReflectiveCodec(c, registry);
		} else if (type instanceof ParameterizedType pt) {
			if (pt.getRawType() instanceof Class<?> c) {
				checkForInnerClass(c);
			}
			return createCodec(pt.getRawType(), registry);
		}
		return null;
	}

	@Override
	public JsonDeserializer<?> createDeserializer(Type type, JsonCodecRegistry registry) {
		if (type instanceof Class<?> c) {
			if (c.isInterface()) {
				return null;
			}
			checkForInnerClass(c);
			if (c.isRecord()) {
				return new RecordDeserializer(c, registry);
			}
			return getReflectiveDeserializer(c, registry);
		} else if (type instanceof ParameterizedType pt) {
			if (pt.getRawType() instanceof Class<?> c) {
				checkForInnerClass(c);
			}
			return createDeserializer(pt.getRawType(), registry);
		}
		return null;
	}

	@Override
	public JsonSerializer<?> createSerializer(Type type, JsonCodecRegistry registry) {
		if (type instanceof Class<?> c) {
			if (c.isInterface()) {
				return null;
			}
			checkForInnerClass(c);
			if (c.isRecord()) {
				return new RecordSerializer(c, registry);
			}
			return getReflectiveSerializer(c, registry);
		} else if (type instanceof ParameterizedType pt) {
			if (pt.getRawType() instanceof Class<?> c) {
				checkForInnerClass(c);
			}
			return createSerializer(pt.getRawType(), registry);
		}
		return null;
	}

	private static void checkForInnerClass(Class<?> c) {
		if (c.isMemberClass()) {
			if (!Modifier.isStatic(c.getModifiers())) {
				throw new IllegalArgumentException("Can not create ReflectiveCodec for non-static inner class \"" + c + "\"");
			}
			checkForInnerClass(c.getDeclaringClass());
		}
	}

	@SuppressWarnings("unchecked")
	private <T> JsonSerializer<T> getReflectiveSerializer(Class<T> tClass, JsonCodecRegistry registry) {
		JsonCodecRoleConstrains codecRole = tClass.getAnnotation(JsonCodecRoleConstrains.class);
		if (codecRole != null && !codecRole.value().isCanSerialize()) {
			return new UnsupportedJsonCodec<>(tClass, registry);
		}
		return (JsonSerializer<T>) new ReflectiveSerializer(tClass, registry);
	}

	@SuppressWarnings("unchecked")
	private <T> JsonDeserializer<T> getReflectiveDeserializer(Class<T> tClass, JsonCodecRegistry registry) {
		JsonCodecRoleConstrains codecRole = tClass.getAnnotation(JsonCodecRoleConstrains.class);
		if (codecRole != null && !codecRole.value().isCanSerialize()) {
			return new UnsupportedJsonCodec<>(tClass, registry);
		}
		return (JsonDeserializer<T>) new ReflectiveDeserializer(tClass, registry);
	}

	private JsonCodec<?> getReflectiveCodec(Class<?> tClass, JsonCodecRegistry registry) {
		JsonCodecRoleConstrains codecRole = tClass.getAnnotation(JsonCodecRoleConstrains.class);
		if (codecRole != null) {
			switch (codecRole.value()) {
				case SERIALIZE -> {
					return SerializeOnlyJsonCodec.ofSerializer(new ReflectiveSerializer(tClass, registry), tClass, registry);
				}
				case DESERIALIZE -> {
					return DeserializeOnlyJsonCodec.ofDeserializer(new ReflectiveDeserializer(tClass, registry), tClass, registry);
				}
				case NONE -> {
					return new UnsupportedJsonCodec<>(tClass, registry);
				}
			}
		}
		return new CombinedJsonCodec<>(new ReflectiveSerializer(tClass, registry), new ReflectiveDeserializer(tClass, registry));
	}

	private JsonCodec<?> getRecordCodec(Class<?> tClass, JsonCodecRegistry registry) {
		JsonCodecRoleConstrains codecRole = tClass.getAnnotation(JsonCodecRoleConstrains.class);
		if (codecRole != null) {
			switch (codecRole.value()) {
				case SERIALIZE -> {
					return SerializeOnlyJsonCodec.ofSerializer(new RecordSerializer(tClass, registry), tClass, registry);
				}
				case DESERIALIZE -> {
					return DeserializeOnlyJsonCodec.ofDeserializer(new RecordDeserializer(tClass, registry), tClass, registry);
				}
				case NONE -> {
					return new UnsupportedJsonCodec<>(tClass, registry);
				}
			}
		}
		return new CombinedJsonCodec<>(new RecordSerializer(tClass, registry), new RecordDeserializer(tClass, registry));
	}

	public static class ReflectiveDeserializer implements JsonDeserializer<Object> {

		final Class<?> tClass;
		final Supplier<Object> constructor;
		final Map<String, FieldCodec> readers;
		final JsonCodecRegistry registry;

		@SuppressWarnings("unchecked")
		public ReflectiveDeserializer(Class<?> tClass, JsonCodecRegistry registry) {
			this.tClass = tClass;
			this.registry = registry;
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
			this.readers = readers;
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
				if (fc == null) {
					reader.skipValue();
					continue;
				}
				try {
					fc.read(reader, o);
				} catch (IllegalAccessException | ArrayIndexOutOfBoundsException e) {
					throw new RuntimeException("" + this.tClass + "#" + fc.name, e);
				}
			}
			reader.endObject();
			if (o instanceof JsonPostDeserializeCall pdc) {
				pdc.postDeserializedJson();
			}
			return o;
		}

		@Override
		public JsonCodecRegistry getRegistry() {
			return registry;
		}
	}

	public static class ReflectiveSerializer implements JsonSerializer<Object> {

		final Class<?> tClass;
		final FieldCodec[] writers;
		final JsonCodecRegistry registry;

		public ReflectiveSerializer(Class<?> tClass, JsonCodecRegistry registry) {
			this.tClass = tClass;
			this.registry = registry;
			this.writers = collectFields(tClass, registry);
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
				} catch (IllegalAccessException | ArrayIndexOutOfBoundsException e) {
					throw new RuntimeException("Field write error: " + this.tClass.getName() + "#" + w.name, e);
				}
			}
			writer.endObject();
		}

		@Override
		public JsonCodecRegistry getRegistry() {
			return registry;
		}
	}

	public static class RecordDeserializer implements JsonDeserializer<Object> {

		final JsonCodecRegistry registry;
		final MultiSupplier<Object> constructor;
		final JsonDeserializer<Object>[] deserializers;
		final String[] names;
		final Map<String, Integer> readers;
		final Class<?>[] components;

		@SuppressWarnings("unchecked")
		public RecordDeserializer(Class<?> tClass, JsonCodecRegistry registry) {
			this.registry = registry;
			MultiSupplier<Object> tmpC;
			var rcs = tClass.getRecordComponents();
			Class<?>[] args = new Class[rcs.length];
			JsonDeserializer<Object>[] des = new JsonDeserializer[rcs.length];
			String[] names = new String[rcs.length];
			Map<String, Integer> readers = new HashMap<>();
			for (int i = 0; i < rcs.length; i++) {
				RecordComponent rc = rcs[i];
				args[i] = rc.getType();
				Object codec = BuiltinCodecFactory.getDefaultCodec(rc, rc.getGenericType(), registry);
				JsonDeserializer<Object> deserializer;
				if (codec instanceof JsonDeserializer<?> jd) {
					deserializer = (JsonDeserializer<Object>) jd;
				} else {
					deserializer = registry.getDeserializerIndirect(rc.getGenericType());
				}
				des[i] = deserializer;
				String name = rc.getName();
				JsonAlias alias = rc.getAnnotation(JsonAlias.class);
				if (alias != null) {
					name = alias.value();
				}
				names[i] = name;
				readers.put(name, i);
			}

			tmpC = ReflectUtils.getMultiConstructor((Class<Object>) tClass, args);
			if (tmpC == null) {
				throw new IllegalArgumentException("Record \"" +
						tClass.getName() +
						"\" have no available canonical constructor and can not be created by ReflectiveCodec");
			}

			this.components = args;
			this.constructor = tmpC;
			this.deserializers = des;
			this.readers = readers;
			this.names = names;
		}

		@Override
		public Object read(JsonReader reader) throws IOException {
			if (reader.nextEntryType() == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			reader.beginObject();
			Object[] args = new Object[names.length];
			while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
				String name = reader.readName();
				Integer index = readers.get(name);
				if (index == null) {
					reader.skipValue();
					continue;
				}
				int i = index;
				try {
					args[i] = deserializers[i].read(reader);
				} catch (Exception e) {
					System.err.println("exception while read field " + name + " " + this.components[i]);
					throw e;
				}

			}
			reader.endObject();

			wrapPrimitives(components, args);
			Object o = constructor.get(args);
			if (o instanceof JsonPostDeserializeCall pdc) {
				pdc.postDeserializedJson();
			}
			return o;
		}

		@Override
		public JsonCodecRegistry getRegistry() {
			return registry;
		}
	}

	private static void wrapPrimitives(Class<?>[] components, Object[] args) {
		for (int i = 0; i < components.length; i++) {
			Class<?> cl = components[i];
			if (cl.isPrimitive() && args[i] == null) {
				if (cl == boolean.class) {
					args[i] = Boolean.FALSE;
				} else if (cl == char.class) {
					args[i] = (char) 0;
				} else {
					args[i] = Numbers.ZERO;
				}
			}
		}
	}

	public static class RecordSerializer implements JsonSerializer<Object> {

		final JsonCodecRegistry registry;
		final JsonSerializer<Object>[] serializers;
		final int nonNullSerializers;
		final String[] names;
		final Function<Object, Object>[] accessors;

		@SuppressWarnings("unchecked")
		public RecordSerializer(Class<?> tClass, JsonCodecRegistry registry) {
			this.registry = registry;
			var rcs = tClass.getRecordComponents();
			JsonSerializer<?>[] ser = new JsonSerializer[rcs.length];
			String[] names = new String[rcs.length];
			Function<Object, Object>[] accessors = new Function[rcs.length];
			int c = 0;
			for (int i = 0; i < rcs.length; i++) {
				RecordComponent rc = rcs[i];
				if (rc.isAnnotationPresent(TransientComponent.class)) {
					continue;
				}
				c++;
				Object codec = BuiltinCodecFactory.getDefaultCodec(rc, rc.getGenericType(), registry);
				if (codec instanceof JsonSerializer<?> js) {
					ser[i] = js;
				} else {
					ser[i] = BuiltinCodecFactory.getUniversalSerializer(rc.getGenericType(), registry);
				}
				String name = rc.getName();
				JsonAlias alias = rc.getAnnotation(JsonAlias.class);
				if (alias != null) {
					name = alias.value();
				}
				Method m = rc.getAccessor();
				m.setAccessible(true);
				accessors[i] = r -> {
					try {
						return m.invoke(r);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};
				names[i] = name;
			}

			this.nonNullSerializers = c;
			this.serializers = (JsonSerializer<Object>[]) ser;
			this.names = names;
			this.accessors = accessors;
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
			if (nonNullSerializers > 0) {
				if (nonNullSerializers > 1) {
					writer.lineBreakEnable(true);
				}
				for (int i = 0; i < serializers.length; i++) {
					JsonSerializer<Object> w = serializers[i];
					if (w == null) continue;
					try {
						writer.writeName(names[i]);
						w.write(accessors[i].apply(value), writer);
					} catch (Exception e) {
						System.err.println("exception while write field " + names[i]);
						throw e;
					}
				}
			}
			writer.endObject();
		}

		@Override
		public JsonCodecRegistry getRegistry() {
			return registry;
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
		final JsonDeserializer<Character> deserializer;

		private CharFieldCodec(Field field, JsonCodecRegistry registry) {
			super(field);
			this.deserializer = registry.getDeserializerIndirect(char.class);
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			writer.writeInt(field.getChar(o));
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			field.setChar(o, deserializer.read(reader));
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

		final JsonSerializer<Object> serializer;
		final JsonDeserializer<Object> deserializer;

		private ObjFieldCodec(Field field, JsonCodecRegistry registry) {
			super(field);
			Type t = field.getGenericType();
			JsonCodec<Object> c = BuiltinCodecFactory.getDefaultCodec(field, t, registry);
			if (c != null) {
				this.serializer = c;
				this.deserializer = c;
			} else {
				this.serializer = BuiltinCodecFactory.getUniversalSerializer(t, registry);
				this.deserializer = registry.getDeserializerIndirect(t);
			}
		}

		@Override
		void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException {
			Object value = field.get(o);
			try {
				serializer.write(value, writer);
			} catch (Exception e) {
				System.err.println("exception while write field " + this.name + " " + this.field.getType());
				throw e;
			}
		}

		@Override
		void read(JsonReader reader, Object o) throws IOException, IllegalAccessException {
			try {
				field.set(o, deserializer.read(reader));
			} catch (Exception e) {
				System.err.println("exception while read field " + this.name + " " + this.field.getType());
				throw e;
			}
		}
	}
}
