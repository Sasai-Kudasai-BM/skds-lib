package net.skds.lib2.io.json.codec;

import lombok.CustomLog;
import net.skds.lib2.io.json.*;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.JsonCodecRoleConstrains;
import net.skds.lib2.io.json.annotation.SkipSerialization;
import net.skds.lib2.io.json.annotation.TransientComponent;
import net.skds.lib2.reflection.ReflectUtils;
import net.skds.lib2.utils.Numbers;
import net.skds.lib2.utils.function.MultiSupplier;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
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
				} catch (Exception e) {
					throw new RuntimeException("Field read error: " + this.tClass + ":" + fc.name, e);
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
			boolean empty = true;
			for (FieldCodec w : writers) {
				try {
					if (!w.checkSkip(value)) {
						writer.writeName(w.name);
						w.write(writer, value);
						empty = false;
					}
				} catch (Exception e) {
					throw new RuntimeException("Field write error: " + this.tClass.getName() + ":" + w.name, e);
				}
			}
			if (empty) {
				writer.lineBreakEnable(false);
			}
			writer.endObject();
		}

		@Override
		public JsonCodecRegistry getRegistry() {
			return registry;
		}
	}

	public static class RecordDeserializer implements JsonDeserializer<Object> {

		final Class<?> tClass;
		final JsonCodecRegistry registry;
		final MultiSupplier<Object> constructor;
		final JsonDeserializer<Object>[] deserializers;
		final String[] names;
		final Map<String, Integer> readers;
		final Class<?>[] components;

		@SuppressWarnings("unchecked")
		public RecordDeserializer(Class<?> tClass, JsonCodecRegistry registry) {
			this.tClass = tClass;
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
					throw new RuntimeException("Exception while read enum component \"" + tClass.getName() + ":" + names[i] + "\"", e);
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

	@SuppressWarnings({ "WrapperTypeMayBePrimitive", "unchecked" })
	private static Predicate<Object> getSkipPredicate(SkipSerialization ss, Class<?> type) {
		Class<? extends Predicate<?>> p = ss.predicate();
		if (p != SkipSerialization.BLANK_PREDICATE) {
			Supplier<? extends Predicate<?>> constructor = ReflectUtils.getConstructor(p);
			if (constructor == null) {
				throw new NullPointerException("constructor of " + p + " is invalid");
			}
			return (Predicate<Object>)constructor.get();
		}
		Predicate<Object> predicate = o -> false;
		if (type.isPrimitive()) {
			if (type == byte.class) {
				byte value = ss.defaultByte();
				predicate = o -> o.equals(value);
			} else if (type == int.class) {
				int value = ss.defaultInt();
				predicate = o -> o.equals(value);
			} else if (type == float.class) {
				float value = ss.defaultFloat();
				predicate = o -> o.equals(value);
			} else if (type == long.class) {
				long value = ss.defaultLong();
				predicate = o -> o.equals(value);
			} else if (type == double.class) {
				double value = ss.defaultDouble();
				predicate = o -> o.equals(value);
			} else if (type == boolean.class) {
				boolean value = ss.defaultBoolean();
				predicate = o -> o.equals(value);
			} else if (type == short.class) {
				short value = ss.defaultShort();
				predicate = o -> o.equals(value);
			} else if (type == char.class) {
				char value = ss.defaultChar();
				predicate = o -> o.equals(value);
			}
		} else {
			if (Number.class.isAssignableFrom(type)) {
				if (type == Byte.class) {
					Byte value = ss.defaultByte();
					predicate = value::equals;
				} else if (type == Integer.class) {
					Integer value = ss.defaultInt();
					predicate = value::equals;
				} else if (type == Float.class) {
					Float value = ss.defaultFloat();
					predicate = value::equals;
				} else if (type == Long.class) {
					Long value = ss.defaultLong();
					predicate = value::equals;
				} else if (type == Double.class) {
					Double value = ss.defaultDouble();
					predicate = value::equals;
				} else if (type == Short.class) {
					Short value = ss.defaultShort();
					predicate = value::equals;
				}
			} else if (type == Boolean.class) {
				Boolean value = ss.defaultBoolean();
				predicate = value::equals;
			} else if (type == Character.class) {
				Character value = ss.defaultChar();
				predicate = value::equals;
			} else if (CharSequence.class.isAssignableFrom(type)) {
				String value = ss.defaultString();
				predicate = value::equals;
			}
			if (ss.skipZeroSize()) {
				if (type.isArray()) {
					predicate = o -> o != null && Array.getLength(o) == 0;
				} else if (Collection.class.isAssignableFrom(type)) {
					predicate = o -> o != null && ((Collection<?>) o).isEmpty();
				} else if (Map.class.isAssignableFrom(type)) {
					predicate = o -> o != null && ((Map<?, ?>) o).isEmpty();
				}
			}
			if (ss.skipNull()) {
				predicate = ((Predicate<Object>) Objects::isNull).or(predicate);
			}

		}
		return predicate;
	}

	public static class RecordSerializer implements JsonSerializer<Object> {

		final Class<?> tClass;
		final JsonCodecRegistry registry;
		final JsonSerializer<Object>[] serializers;
		final int nonNullSerializers;
		final String[] names;
		final Function<Object, Object>[] accessors;
		final Predicate<Object>[] skipPredicates;

		@SuppressWarnings("unchecked")
		public RecordSerializer(Class<?> tClass, JsonCodecRegistry registry) {
			this.tClass = tClass;
			this.registry = registry;
			var rcs = tClass.getRecordComponents();
			Predicate<Object>[] skips = new Predicate[rcs.length];
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
				SkipSerialization ss = rc.getAnnotation(SkipSerialization.class);
				if (ss != null) {
					skips[i] = getSkipPredicate(ss, rc.getType());
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

			this.skipPredicates = skips;
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
				boolean empty = true;
				for (int i = 0; i < serializers.length; i++) {
					JsonSerializer<Object> w = serializers[i];
					if (w == null) continue;
					Predicate<Object> predicate = skipPredicates[i];
					try {
						Object val = accessors[i].apply(value);
						if (predicate != null && predicate.test(val)) {
							continue;
						}
						writer.writeName(names[i]);
						w.write(val, writer);
						empty = false;
					} catch (Exception e) {
						throw new RuntimeException("Exception while write enum component \"" + tClass.getName() + ":" + names[i] + "\"", e);
					}
				}
				if (empty) {
					writer.lineBreakEnable(false);
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
		protected final SkipSerialization skipSerialization;

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
			this.skipSerialization = field.getAnnotation(SkipSerialization.class);
		}

		abstract void write(JsonWriter writer, Object o) throws IOException, IllegalAccessException;

		abstract void read(JsonReader reader, Object o) throws IOException, IllegalAccessException;

		abstract boolean checkSkip(Object o) throws IllegalAccessException;
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			byte value = field.getByte(o);
			return value == skipSerialization.defaultByte();
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			short value = field.getShort(o);
			return value == skipSerialization.defaultShort();
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			int value = field.getInt(o);
			return value == skipSerialization.defaultInt();
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			long value = field.getLong(o);
			return value == skipSerialization.defaultLong();
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			float value = field.getFloat(o);
			return value == skipSerialization.defaultFloat();
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			double value = field.getDouble(o);
			return value == skipSerialization.defaultDouble();
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			char value = field.getChar(o);
			return value == skipSerialization.defaultChar();
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

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			boolean value = field.getBoolean(o);
			return value == skipSerialization.defaultBoolean();
		}
	}

	private static class ObjFieldCodec extends FieldCodec {

		final JsonSerializer<Object> serializer;
		final JsonDeserializer<Object> deserializer;
		final Predicate<Object> skipPredicate;

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
			this.skipPredicate = this.skipSerialization == null ? o -> false : getSkipPredicate(this.skipSerialization, field.getType());
		}

		@Override
		void write(JsonWriter writer, Object o) throws IllegalAccessException {
			Object value = field.get(o);
			try {
				serializer.write(value, writer);
			} catch (Exception e) {
				throw new RuntimeException("Field while error: " + this.field.getDeclaringClass().getName() + ":" + this.field.getName(), e);
			}
		}

		@Override
		void read(JsonReader reader, Object o) {
			try {
				field.set(o, deserializer.read(reader));
			} catch (Exception e) {
				throw new RuntimeException("Field read error: " + this.field.getDeclaringClass().getName() + ":" + this.field.getName(), e);
			}
		}

		@Override
		boolean checkSkip(Object o) throws IllegalAccessException {
			if (skipSerialization == null) return false;
			Object value = field.get(o);
			return skipPredicate.test(value);
		}
	}
}
