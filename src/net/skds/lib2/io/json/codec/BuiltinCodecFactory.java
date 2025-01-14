package net.skds.lib2.io.json.codec;

import lombok.CustomLog;
import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.elements.*;
import net.skds.lib2.io.json.exception.JsonReadException;
import net.skds.lib2.reflection.ReflectUtils;
import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.StringUtils;
import net.skds.lib2.utils.collection.ImmutableArrayHashMap;
import net.skds.lib2.utils.function.MultiSupplier;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

@CustomLog
public class BuiltinCodecFactory implements JsonCodecFactory {

	public static final BuiltinCodecFactory INSTANCE = new BuiltinCodecFactory();

	final Map<Type, JsonCodecFactory> map = new ImmutableArrayHashMap<>(
			JsonObject.class, (JsonCodecFactory) JsonObject.Codec::new,
			JsonElement.class, (JsonCodecFactory) JsonElement.Codec::new,
			JsonString.class, (JsonCodecFactory) JsonString.Codec::new,
			JsonNumber.class, (JsonCodecFactory) JsonNumber.Codec::new,
			JsonBoolean.class, (JsonCodecFactory) JsonBoolean.Codec::new,
			JsonArray.class, (JsonCodecFactory) JsonArray.Codec::new,

			String.class, (JsonCodecFactory) StringCodec::new,

			Number.class, (JsonCodecFactory) NumberCodec::new,
			Byte.class, (JsonCodecFactory) WrappedByteCodec::new,
			Boolean.class, (JsonCodecFactory) WrappedBooleanCodec::new,
			Short.class, (JsonCodecFactory) WrappedShortCodec::new,
			Character.class, (JsonCodecFactory) WrappedCharCodec::new,
			Integer.class, (JsonCodecFactory) WrappedIntCodec::new,
			Long.class, (JsonCodecFactory) WrappedLongCodec::new,
			Float.class, (JsonCodecFactory) WrappedFloatCodec::new,
			Double.class, (JsonCodecFactory) WrappedDoubleCodec::new,

			byte.class, (JsonCodecFactory) ByteCodec::new,
			boolean.class, (JsonCodecFactory) BooleanCodec::new,
			short.class, (JsonCodecFactory) ShortCodec::new,
			char.class, (JsonCodecFactory) CharCodec::new,
			int.class, (JsonCodecFactory) IntCodec::new,
			long.class, (JsonCodecFactory) LongCodec::new,
			float.class, (JsonCodecFactory) FloatCodec::new,
			double.class, (JsonCodecFactory) DoubleCodec::new
	);

	@Override
	public JsonSerializer<?> createSerializer(Type type, JsonCodecRegistry registry) {
		JsonCodecFactory fac = map.get(type);
		if (fac != null) {
			return fac.createSerializer(type, registry);
		}
		if (type instanceof Class<?> cl) {
			if (Collection.class.isAssignableFrom(cl)) {
				return new CollectionSerializer(registry);
			}
			if (Map.class.isAssignableFrom(cl)) {
				return new MapSerializer(registry);
			}
		} else if (type instanceof ParameterizedType pt) {
			if (pt.getRawType() instanceof Class<?> cl) {
				// Non-canonical map
				if (Map.class.isAssignableFrom(cl)) {
					Type[] args = pt.getActualTypeArguments();
					if (args.length != 2) {
						return new MapSerializer(registry);
					}
				}
			}
		}
		return createCodec(type, registry);
	}

	@Override
	public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
		JsonCodecFactory fac = map.get(type);
		if (fac != null) {
			return fac.createCodec(type, registry);
		}
		if (type instanceof Class<?> cl) {
			{
				JsonCodec<?> codec = getDefaultCodec(cl, cl, registry);
				if (codec != null) {
					return codec;
				}
			}
			if (cl.isEnum()) {
				return new EnumCodec<>(type, registry);
			} else if (cl.isArray()) {
				Class<?> cle = cl.componentType();
				if (cle.isPrimitive()) {
					if (cle == int.class) {
						return new IntArrayCodec(registry);
					} else if (cle == float.class) {
						return new FloatArrayCodec(registry);
					} else if (cle == double.class) {
						return new DoubleArrayCodec(registry);
					} else if (cle == boolean.class) {
						return new BooleanArrayCodec(registry);
					} else if (cle == long.class) {
						return new LongArrayCodec(registry);
					} else if (cle == byte.class) {
						return new ByteArrayCodec(registry);
					} else if (cle == short.class) {
						return new ShortArrayCodec(registry);
					} else if (cle == char.class) {
						return new CharArrayCodec(registry);
					}
					throw new RuntimeException("Unknown primitive " + cle);
				} else {
					JsonDeserializer<Object> deserializer = registry.getDeserializerIndirect(cle);
					JsonSerializer<Object> serializer = getUniversalSerializer(cle, registry);
					return new ArrayCodec(cle, deserializer, serializer, registry);
				}
			}
		} else if (type instanceof ParameterizedType pt) {
			if (!(pt.getRawType() instanceof Class<?> cl))
				throw new UnsupportedOperationException("Unsupported or wildcard type \"" + pt + "\"");

			if (Map.class.isAssignableFrom(cl)) {
				return new MapCodec(cl, pt.getActualTypeArguments(), registry);
			} else if (Collection.class.isAssignableFrom(cl)) {
				if (Set.class.isAssignableFrom(cl)) {
					return new CollectionCodec(cl, pt.getActualTypeArguments(), registry, HashSet::new);
				} else {
					return new CollectionCodec(cl, pt.getActualTypeArguments(), registry, ArrayList::new);
				}
			}
		}

		return ReflectiveJsonCodecFactory.INSTANCE.createCodec(type, registry);
	}

	private static boolean isFinal(Type type) {
		if (type instanceof Class<?> cl) {
			return Modifier.isFinal(cl.getModifiers()) || cl.isRecord() || cl.isEnum();
		}
		return false;
	}

	public static JsonSerializer<Object> getUniversalSerializer(Type type, JsonCodecRegistry registry) {
		if (isFinal(type) || type instanceof ParameterizedType) {
			return registry.getSerializerIndirect(type);
		}
		return new AbstractJsonSerializer<>(registry) {
			JsonSerializer<Object> serializer;

			@Override
			public void write(Object value, JsonWriter writer) throws IOException {
				if (value == null) {
					writer.writeNull();
					return;
				}
				JsonSerializer<Object> s = serializer;
				if (s == null) {
					s = this.registry.getSerializerNullable(type);
					this.serializer = Objects.requireNonNullElse(s, this);
				}
				if (this.serializer == this) {
					this.registry.getSerializer((Type) value.getClass()).write(value, writer);
				} else {
					this.serializer.write(value, writer);
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static JsonCodec<Object> getDefaultCodec(AnnotatedElement annotatedElement, Type type, JsonCodecRegistry
			registry) {
		DefaultJsonCodec defaultCodec = annotatedElement.getAnnotation(DefaultJsonCodec.class);
		if (defaultCodec == null) return null;
		Class<?> factoryClass = defaultCodec.value();
		if (JsonCodecFactory.class.isAssignableFrom(factoryClass)) {
			Supplier<JsonCodecFactory> constructor = (Supplier<JsonCodecFactory>) ReflectUtils.getConstructor(factoryClass);
			if (constructor != null) {
				JsonCodecFactory factory = constructor.get();
				if (factory != null) {
					JsonCodec<?> codec = factory.createCodec(type, registry);
					if (codec != null) return (JsonCodec<Object>) codec;
				}
			}
		} else l1:{
			MultiSupplier<Object> constructor =
					(MultiSupplier<Object>) ReflectUtils.getMultiConstructor(factoryClass, Type.class, JsonCodecRegistry.class);
			if (constructor != null) {
				if (!JsonCodec.class.isAssignableFrom(factoryClass)) {

					if (!JsonSerializer.class.isAssignableFrom(factoryClass) && !JsonDeserializer.class.isAssignableFrom(factoryClass)) {
						break l1;
					}
					JsonDeserializer<?> deserializer;
					JsonSerializer<?> serializer;
					if (!JsonSerializer.class.isAssignableFrom(factoryClass)) {
						serializer = ReflectiveJsonCodecFactory.INSTANCE.createSerializer(type, registry);
					} else {
						serializer = (JsonSerializer<?>) constructor.get(type, registry);
					}
					if (!JsonDeserializer.class.isAssignableFrom(factoryClass)) {
						deserializer = ReflectiveJsonCodecFactory.INSTANCE.createDeserializer(type, registry);
					} else {
						deserializer = (JsonDeserializer<?>) constructor.get(type, registry);
					}
					return new CombinedJsonCodec<>(serializer, deserializer);
				}
				return (JsonCodec<Object>) constructor.get(type, registry);
			}
		}
		log.error("Invalid @DefaultJsonCodec on \"" + annotatedElement + "\"");
		return null;
	}


	@SuppressWarnings("ClassCanBeRecord")
	public static class CollectionSerializer implements JsonSerializer<Collection<Object>> {

		final JsonCodecRegistry registry;

		public CollectionSerializer(JsonCodecRegistry registry) {
			this.registry = registry;
		}

		@Override
		public void write(Collection<Object> value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.size();
			if (size > 1) {
				writer.lineBreakEnable(true);
			}
			for (Object v : value) {
				if (v == null) {
					writer.writeNull();
					continue;
				}
				registry.getSerializer((Type) v.getClass()).write(v, writer);
			}
			writer.endArray();
		}

		@Override
		public JsonCodecRegistry getRegistry() {
			return registry;
		}
	}

	@SuppressWarnings("ClassCanBeRecord")
	public static class MapSerializer implements JsonSerializer<Map<Object, Object>> {

		final JsonCodecRegistry registry;

		public MapSerializer(JsonCodecRegistry registry) {
			this.registry = registry;
		}

		@Override
		public void write(Map<Object, Object> value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginObject();
			int size = value.size();
			if (size > 1) {
				writer.lineBreakEnable(true);
			}
			for (var e : value.entrySet()) {
				String ks;
				Object k = e.getKey();
				if (k == null) {
					ks = "null";
				} else {
					ks = registry.getSerializer((Type) k.getClass()).valueAsKeyString(k);
				}
				writer.writeName(ks);
				Object v = e.getValue();
				if (v == null) {
					writer.writeNull();
					continue;
				}
				registry.getSerializer((Type) v.getClass()).write(v, writer);
			}
			writer.endObject();
		}

		@Override
		public JsonCodecRegistry getRegistry() {
			return registry;
		}
	}


	public static class MapCodec extends AbstractJsonCodec<Map<Object, Object>> {

		//final Class<?> tClass;
		final Supplier<Map<Object, Object>> constructor;
		final JsonDeserializer<Object> keyDeserializer;
		final JsonDeserializer<Object> elementDeserializer;
		final JsonSerializer<Object> keySerializer;
		final JsonSerializer<Object> valueSerializer;

		@SuppressWarnings({"unchecked", "rawtypes"})
		public MapCodec(Class<?> tClass, Type[] parameters, JsonCodecRegistry registry) {
			super(tClass, registry);
			//this.tClass = tClass;
			if (parameters.length != 2) {
				throw new IllegalArgumentException("Unable to create codec for non-canonical map declaration \"" + tClass.getName()
						+ " " + Arrays.toString(parameters) + "\"");
			}
			this.keyDeserializer = registry.getDeserializerIndirect(parameters[0]);
			this.elementDeserializer = registry.getDeserializerIndirect(parameters[1]);
			this.keySerializer = getUniversalSerializer(parameters[0], registry);
			this.valueSerializer = getUniversalSerializer(parameters[1], registry);
			Supplier<Map<Object, Object>> tmpC;
			if (tClass.isInterface() || Modifier.isAbstract(tClass.getModifiers())) {
				tmpC = HashMap::new;
			} else if (EnumMap.class.isAssignableFrom(tClass)) {
				tmpC = () -> {
					Map map = new EnumMap<>((Class) parameters[0]);
					return (Map<Object, Object>) map;
				};
			} else {
				tmpC = (Supplier<Map<Object, Object>>) ReflectUtils.getConstructor(tClass);
				if (tmpC == null) {
					log.warn("Class \"" + tClass.getName() + "\" have no empty constructor! HashMap will be used instead");
					tmpC = HashMap::new;
				}
			}
			this.constructor = tmpC;
		}

		@Override
		public void write(Map<Object, Object> value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginObject();
			int size = value.size();
			if (size > 1) {
				writer.lineBreakEnable(true);
			}

			for (Map.Entry<Object, Object> entry : value.entrySet()) {
				Object k = entry.getKey();
				Object v = entry.getValue();
				writer.writeName(keySerializer.valueAsKeyString(k));
				valueSerializer.write(v, writer);
			}
			writer.endObject();
		}

		@Override
		public Map<Object, Object> read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					Map<Object, Object> map = constructor.get();
					while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
						Object key = keyDeserializer.read(reader);
						reader.readDotDot();
						Object value = elementDeserializer.read(reader);
						map.put(key, value);
					}
					reader.endObject();
					return map;
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static class CollectionCodec extends AbstractJsonCodec<Collection<Object>> {

		final Supplier<Collection<Object>> constructor;
		final JsonDeserializer<Object> deserializer;
		final JsonSerializer<Object> serializer;

		@SuppressWarnings("unchecked")
		public CollectionCodec(Class<?> tClass, Type[] parameters, JsonCodecRegistry registry, Supplier<Collection<Object>> defaultSupplier) {
			super(tClass, registry);
			this.deserializer = registry.getDeserializerIndirect(parameters[0]);
			this.serializer = getUniversalSerializer(parameters[0], registry);
			Supplier<Collection<Object>> tmpC;
			if (tClass.isInterface() || Modifier.isAbstract(tClass.getModifiers())) {
				tmpC = defaultSupplier;
			} else {
				tmpC = (Supplier<Collection<Object>>) ReflectUtils.getConstructor(tClass);
				if (tmpC == null) {
					log.warn("Class \"" + tClass.getName() + "\" have no empty constructor! Default supplier will be used instead");
					tmpC = defaultSupplier;
				}
			}
			this.constructor = tmpC;
		}

		@Override
		public void write(Collection<Object> value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.size();
			if (size > 1) {
				writer.lineBreakEnable(true);
			}
			for (Object v : value) {
				serializer.write(v, writer);
			}
			writer.endArray();
		}

		@Override
		public Collection<Object> read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					Collection<Object> list = constructor.get();
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						Object value = deserializer.read(reader);
						list.add(value);
					}
					reader.endArray();
					return list;
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}


	public static class ArrayCodec extends AbstractJsonCodec<Object> {

		final Class<?> tClass;
		final JsonDeserializer<Object> deserializer;
		final JsonSerializer<Object> serializer;
		final Object[] array;

		public ArrayCodec(Class<?> type, JsonCodecRegistry registry) {
			this(type, registry.getDeserializerIndirect(type), registry.getSerializerIndirect(type), registry);
		}

		public ArrayCodec(Class<?> tClass, JsonDeserializer<Object> deserializer, JsonSerializer<Object> serializer, JsonCodecRegistry registry) {
			super(tClass, registry);
			this.tClass = tClass;
			this.deserializer = deserializer;
			this.serializer = serializer;
			this.array = (Object[]) Array.newInstance(tClass, 0);
		}

		@Override
		public void write(Object value, JsonWriter writer) throws IOException {
			write(value, writer, this.serializer);
		}

		@SuppressWarnings("unchecked")
		public static <T> void write(Object value, JsonWriter writer, JsonSerializer<T> serializer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = Array.getLength(value);
			if (size > 1) {
				writer.lineBreakEnable(true);
			}
			for (int i = 0; i < size; i++) {
				T v = (T) Array.get(value, i);
				serializer.write(v, writer);
			}
			writer.endArray();
		}

		@Override
		public Object read(JsonReader reader) throws IOException {
			return read(this.array, reader, this.deserializer);
		}

		public static <T> T[] read(T[] emptyArray, JsonReader reader, JsonDeserializer<T> deserializer) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayList<Object> list = new ArrayList<>();
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						list.add(deserializer.read(reader));
					}
					reader.endArray();
					return list.toArray(emptyArray);
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class IntArrayCodec extends AbstractJsonCodec<int[]> {

		public IntArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(int[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeInt(value[i]);
			}
			writer.endArray();
		}

		@Override
		public int[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.IntGrowingArray array = new ArrayUtils.IntGrowingArray(16);
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						array.add(reader.readInt());
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class ByteArrayCodec extends AbstractJsonCodec<byte[]> {

		public ByteArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(byte[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeInt(value[i]);
			}
			writer.endArray();
		}

		@Override
		public byte[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.ByteGrowingArray array = new ArrayUtils.ByteGrowingArray(16);
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						array.add((byte) reader.readInt());
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class BooleanArrayCodec extends AbstractJsonCodec<boolean[]> {

		public BooleanArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(boolean[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeBoolean(value[i]);
			}
			writer.endArray();
		}

		@Override
		public boolean[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.BooleanGrowingArray array = new ArrayUtils.BooleanGrowingArray(16);
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						array.add(reader.readBoolean());
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class ShortArrayCodec extends AbstractJsonCodec<short[]> {

		public ShortArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(short[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeInt(value[i]);
			}
			writer.endArray();
		}

		@Override
		public short[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.ShortGrowingArray array = new ArrayUtils.ShortGrowingArray(16);
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						array.add((short) reader.readInt());
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class CharArrayCodec extends AbstractJsonCodec<char[]> {

		public CharArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(char[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeInt(value[i]);
			}
			writer.endArray();
		}

		@Override
		public char[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.CharGrowingArray array = new ArrayUtils.CharGrowingArray(16);
					l1:
					while (true) {
						switch (reader.nextEntryType()) {
							case NULL -> {
								reader.skipNull();
								array.add((char) 0);
							}
							case NUMBER -> {
								Number n = reader.readNumber();
								array.add((char) n.intValue());
							}
							case STRING -> {
								String cs = reader.readString();
								if (cs.length() == 1) {
									array.add(cs.charAt(0));
								} else {
									throw new JsonReadException("Unexpected char " + cs);
								}
							}
							case END_ARRAY -> {
								break l1;
							}
							default -> throw new JsonReadException("Unexpected token " + type);
						}
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class LongArrayCodec extends AbstractJsonCodec<long[]> {

		public LongArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(long[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeInt(value[i]);
			}
			writer.endArray();
		}

		@Override
		public long[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.LongGrowingArray array = new ArrayUtils.LongGrowingArray(16);
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						array.add(reader.readLong());
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}


	public static final class FloatArrayCodec extends AbstractJsonCodec<float[]> {

		public FloatArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(float[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeFloat(value[i]);
			}
			writer.endArray();
		}

		@Override
		public float[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.FloatGrowingArray array = new ArrayUtils.FloatGrowingArray(16);
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						array.add(reader.readFloat());
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class DoubleArrayCodec extends AbstractJsonCodec<double[]> {

		public DoubleArrayCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(double[] value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeFloat(value[i]);
			}
			writer.endArray();
		}

		@Override
		public double[] read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					ArrayUtils.DoubleGrowingArray array = new ArrayUtils.DoubleGrowingArray(16);
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						array.add(reader.readDouble());
					}
					reader.endArray();
					return array.getArray();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}


	public static final class StringCodec extends AbstractJsonCodec<String> {

		final JsonDeserializer<JsonObject> jod;
		final JsonDeserializer<JsonArray> jad;

		public StringCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
			this.jod = registry.getDeserializer(JsonObject.class);
			this.jad = registry.getDeserializer(JsonArray.class);
		}

		@Override
		public void write(String value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeString(value);
		}

		@Override
		public String read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					return reader.readString();
				}
				case NUMBER -> {
					return String.valueOf(reader.readNumber());
				}
				case BOOLEAN -> {
					return String.valueOf(reader.readBoolean());
				}
				case BEGIN_OBJECT -> {
					return jod.read(reader).toString();    // TODO
				}
				case BEGIN_ARRAY -> {
					return jad.read(reader).toString();    // TODO
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class EnumCodec<E extends Enum<E>> extends AbstractJsonCodec<E> {

		private final Class<E> eClass;

		@SuppressWarnings("unchecked")
		public EnumCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
			eClass = (Class<E>) type;
		}

		@Override
		public String valueAsKeyString(E val) {
			return val.name();
		}

		@Override
		public void write(E value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeString(value.name());
		}

		@Override
		public E read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					String s = reader.readString();
					try {
						return Enum.valueOf(eClass, s);
					} catch (IllegalArgumentException e) {
						return null;
					}
				}
				case NUMBER -> {
					int n = reader.readInt();
					E[] values = eClass.getEnumConstants();
					if (n < 0 || n >= values.length) return null;
					return values[n];
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class IntCodec extends AbstractJsonCodec<Integer> {

		public IntCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Integer value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeRaw(value.toString());
		}

		@Override
		public Integer read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			Number n = reader.readNumber();
			return n.intValue();
		}
	}

	public static final class WrappedIntCodec extends AbstractJsonCodec<Integer> {

		public WrappedIntCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Integer value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeRaw(value.toString());
		}

		@Override
		public Integer read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			Number n = reader.readNumber();
			return n.intValue();
		}
	}

	public static final class ByteCodec extends AbstractJsonCodec<Byte> {

		public ByteCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Byte value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeRaw(value.toString());
		}

		@Override
		public Byte read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			Number n = reader.readNumber();
			return n.byteValue();
		}
	}

	public static final class WrappedByteCodec extends AbstractJsonCodec<Byte> {

		public WrappedByteCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Byte value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeRaw(value.toString());
		}

		@Override
		public Byte read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			Number n = reader.readNumber();
			return n.byteValue();
		}
	}

	public static final class NumberCodec extends AbstractJsonCodec<Number> {

		public NumberCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Number value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeRaw(value.toString());
		}

		@Override
		public Number read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readNumber();
		}
	}

	public static final class BooleanCodec extends AbstractJsonCodec<Boolean> {

		public BooleanCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Boolean value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeBoolean(false);
				return;
			}
			writer.writeBoolean(value);
		}

		@Override
		public Boolean read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return false;
			}
			return reader.readBoolean();
		}
	}

	public static final class WrappedBooleanCodec extends AbstractJsonCodec<Boolean> {

		public WrappedBooleanCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Boolean value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeBoolean(value);
		}

		@Override
		public Boolean read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readBoolean();
		}
	}

	public static final class CharCodec extends AbstractJsonCodec<Character> {

		public CharCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Character value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeString(StringUtils.unicodeCharUC(value));
		}

		@Override
		public Character read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return (char) n.intValue();
				}
				case STRING -> {
					String cs = reader.readString();
					if (cs.length() == 1) {
						return cs.charAt(0);
					} else {
						throw new JsonReadException("Unexpected char " + cs);
					}
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class WrappedCharCodec extends AbstractJsonCodec<Character> {

		public WrappedCharCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Character value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeString(StringUtils.unicodeCharUC(value));
		}

		@Override
		public Character read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return (char) n.intValue();
				}
				case STRING -> {
					String cs = reader.readString();
					if (cs.length() == 1) {
						return cs.charAt(0);
					} else {
						throw new JsonReadException("Unexpected char " + cs);
					}
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class ShortCodec extends AbstractJsonCodec<Short> {

		public ShortCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Short value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Short read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			Number n = reader.readNumber();
			return n.shortValue();
		}
	}

	public static final class WrappedShortCodec extends AbstractJsonCodec<Short> {

		public WrappedShortCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Short value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Short read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			Number n = reader.readNumber();
			return n.shortValue();
		}
	}

	public static final class LongCodec extends AbstractJsonCodec<Long> {

		public LongCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Long value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Long read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return 0L;
			}
			Number n = reader.readNumber();
			return n.longValue();
		}
	}

	public static final class WrappedLongCodec extends AbstractJsonCodec<Long> {

		public WrappedLongCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Long value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Long read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			Number n = reader.readNumber();
			return n.longValue();
		}
	}

	public static final class FloatCodec extends AbstractJsonCodec<Float> {

		public FloatCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Float value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeFloat(value);
		}

		@Override
		public Float read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return 0f;
			}
			Number n = reader.readNumber();
			return n.floatValue();
		}
	}

	public static final class WrappedFloatCodec extends AbstractJsonCodec<Float> {

		public WrappedFloatCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Float value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeFloat(value);
		}

		@Override
		public Float read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			Number n = reader.readNumber();
			return n.floatValue();
		}
	}

	public static final class DoubleCodec extends AbstractJsonCodec<Double> {

		public DoubleCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Double value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeFloat(value);
		}

		@Override
		public Double read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return 0d;
			}
			Number n = reader.readNumber();
			return n.doubleValue();
		}
	}

	public static final class WrappedDoubleCodec extends AbstractJsonCodec<Double> {

		public WrappedDoubleCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Double value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeFloat(value);
		}

		@Override
		public Double read(JsonReader reader) throws IOException {
			JsonEntryType type = reader.nextEntryType();
			if (type == JsonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			Number n = reader.readNumber();
			return n.doubleValue();
		}
	}
}
