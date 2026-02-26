package net.skds.lib2.io.codec;

import lombok.CustomLog;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.codec.annotation.DefaultEnumTypedCodec;
import net.skds.lib2.io.codec.annotation.EnumNameDataFixer;
import net.skds.lib2.io.codec.nulls.NullCodec;
import net.skds.lib2.io.codec.typed.ConfigEnumType;
import net.skds.lib2.io.codec.typed.TypedEnumAdapter;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.json.elements.*;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.lib2.reflection.ReflectUtils;
import net.skds.lib2.utils.*;
import net.skds.lib2.utils.collection.ImmutableArrayHashMap;
import net.skds.lib2.utils.function.MultiSupplier;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Time;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

import static net.skds.lib2.io.sosison.SosisonEntryType.END_OBJECT;

@CustomLog
public class BuiltinCodecFactory implements CodecFactory {

	public static final BuiltinCodecFactory INSTANCE = new BuiltinCodecFactory();

	final Map<Type, CodecFactory> map = new ImmutableArrayHashMap<>(
			JsonObject.class, (CodecFactory) JsonObject.Codec::new,
			JsonElement.class, (CodecFactory) JsonElement.Codec::new,
			JsonString.class, (CodecFactory) JsonString.Codec::new,
			JsonNumber.class, (CodecFactory) JsonNumber.Codec::new,
			JsonBoolean.class, (CodecFactory) JsonBoolean.Codec::new,
			JsonArray.class, (CodecFactory) JsonArray.Codec::new,
			JsonElement.JsonNull.class, (CodecFactory) NullCodec::new,

			String.class, (CodecFactory) StringCodec::new,
			Object.class, (CodecFactory) ObjectCodec::new,
			File.class, (CodecFactory) FileCodec::new,
			Path.class, (CodecFactory) PathCodec::new,
			URI.class, (CodecFactory) URICodec::new,
			URL.class, (CodecFactory) URLCodec::new,
			UUID.class, (CodecFactory) UUIDCodec::new,
			Color.class, (CodecFactory) ColorCodec::new,
			Date.class, (CodecFactory) DateCodec::new,

			Number.class, (CodecFactory) NumberCodec::new,
			Byte.class, (CodecFactory) WrappedByteCodec::new,
			Boolean.class, (CodecFactory) WrappedBooleanCodec::new,
			Short.class, (CodecFactory) WrappedShortCodec::new,
			Character.class, (CodecFactory) WrappedCharCodec::new,
			Integer.class, (CodecFactory) WrappedIntCodec::new,
			Long.class, (CodecFactory) WrappedLongCodec::new,
			Float.class, (CodecFactory) WrappedFloatCodec::new,
			Double.class, (CodecFactory) WrappedDoubleCodec::new,

			byte.class, (CodecFactory) ByteCodec::new,
			boolean.class, (CodecFactory) BooleanCodec::new,
			short.class, (CodecFactory) ShortCodec::new,
			char.class, (CodecFactory) CharCodec::new,
			int.class, (CodecFactory) IntCodec::new,
			long.class, (CodecFactory) LongCodec::new,
			float.class, (CodecFactory) FloatCodec::new,
			double.class, (CodecFactory) DoubleCodec::new
	);

	@Override
	public UniversalSerializer<?> createSerializer(Type type, CodecRegistry registry) {
		CodecFactory fac = map.get(type);
		if (fac != null) {
			return fac.createSerializer(type, registry);
		}
		if (type instanceof Class<?> cl) {
			UniversalCodec<?> codec = getDefaultCodec(cl, cl, registry);
			if (codec != null) {
				return codec;
			}
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
	public UniversalDeserializer<?> createDeserializer(Type type, CodecRegistry registry) {
		if (type instanceof Class<?> cl) {
			if (Map.class.isAssignableFrom(cl)) {
				return new MapCodec(cl, registry);
			}
			if (Collection.class.isAssignableFrom(cl)) {
				return new CollectionCodec(cl, registry);
			}
		}
		return createCodec(type, registry);
	}

	@Override
	public UniversalCodec<?> createCodec(Type type, CodecRegistry registry) {
		CodecFactory fac = map.get(type);
		if (fac != null) {
			return fac.createCodec(type, registry);
		}
		if (type instanceof Class<?> cl) {
			{
				UniversalCodec<?> codec = getDefaultCodec(cl, cl, registry);
				if (codec != null) {
					return codec;
				}
			}
			if (cl.isEnum()) {
				return new EnumCodec<>(cl, registry);
			} else if (cl.getSuperclass() != null && cl.getSuperclass().isEnum()) {
				return new EnumCodec<>(cl.getSuperclass(), registry);
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
					UniversalDeserializer<Object> deserializer = registry.getDeserializerIndirect(cle);
					UniversalSerializer<Object> serializer = getUniversalSerializer(cle, registry);
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
					return new CollectionCodec(cl, pt.getActualTypeArguments(), HashSet::new, registry);
				} else {
					return new CollectionCodec(cl, pt.getActualTypeArguments(), ArrayList::new, registry);
				}
			}
		} else if (type instanceof GenericArrayType gat) {
			Type cle = gat.getGenericComponentType();
			Class<?> raw = ReflectUtils.getRawType(cle);
			UniversalDeserializer<Object> deserializer = registry.getDeserializerIndirect(cle);
			UniversalSerializer<Object> serializer = getUniversalSerializer(cle, registry);
			return new ArrayCodec(raw, deserializer, serializer, registry);
		}

		return ReflectiveCodecFactory.INSTANCE.createCodec(type, registry);
	}

	private static boolean isFinal(Type type) {
		if (type instanceof Class<?> cl) {
			return Modifier.isFinal(cl.getModifiers()) || cl.isRecord();
		}
		return false;
	}

	public static UniversalSerializer<Object> getUniversalSerializer(Type type, CodecRegistry registry) {
		if (isFinal(type) || type instanceof ParameterizedType) {
			return registry.getSerializerIndirect(type);
		}
		return new AbstractUniversalSerializer<>(registry) {
			UniversalSerializer<Object> serializer;

			@Override
			public void write(Object value, UniversalWriter writer) throws IOException {
				if (value == null) {
					writer.writeNull();
					return;
				}
				UniversalSerializer<Object> s = serializer;
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
	public static UniversalCodec<Object> getDefaultCodec(AnnotatedElement annotatedElement,
														 Type type,
														 CodecRegistry registry
	) {
		DefaultCodec defaultCodec = annotatedElement.getAnnotation(DefaultCodec.class);
		if (defaultCodec == null) {
			if (!(type instanceof ParameterizedType pt
					&& pt.getRawType() instanceof Class<?> rtc
					&& (defaultCodec = rtc.getAnnotation(DefaultCodec.class)) != null
			)) {
				return getDefaultEnumTypedCodec(annotatedElement, type, registry);
			}
		}
		Class<?> factoryClass = defaultCodec.value();
		if (CodecFactory.class.isAssignableFrom(factoryClass)) {
			Supplier<CodecFactory> constructor = (Supplier<CodecFactory>) ReflectUtils.getConstructor(factoryClass);
			if (constructor != null) {
				CodecFactory factory = constructor.get();
				if (factory != null) {
					UniversalCodec<?> codec = factory.createCodec(type, registry);
					if (codec != null) return (UniversalCodec<Object>) codec;
				}
			}
		} else l1:{
			MultiSupplier<Object> constructor =
					(MultiSupplier<Object>) ReflectUtils.getMultiConstructor(factoryClass, Type.class, CodecRegistry.class);
			if (constructor != null) {
				if (!UniversalCodec.class.isAssignableFrom(factoryClass)) {

					if (!UniversalSerializer.class.isAssignableFrom(factoryClass) && !UniversalDeserializer.class.isAssignableFrom(factoryClass)) {
						break l1;
					}
					UniversalDeserializer<?> deserializer;
					UniversalSerializer<?> serializer;
					if (!UniversalSerializer.class.isAssignableFrom(factoryClass)) {
						serializer = ReflectiveCodecFactory.INSTANCE.createSerializer(type, registry);
					} else {
						serializer = (UniversalSerializer<?>) constructor.get(type, registry);
					}
					if (!UniversalDeserializer.class.isAssignableFrom(factoryClass)) {
						deserializer = ReflectiveCodecFactory.INSTANCE.createDeserializer(type, registry);
					} else {
						deserializer = (UniversalDeserializer<?>) constructor.get(type, registry);
					}
					return new CombinedCodec<>(serializer, deserializer);
				}
				return (UniversalCodec<Object>) constructor.get(type, registry);
			}
		}
		log.error("Invalid @DefaultJsonCodec on \"" + annotatedElement + "\"");
		return null;
	}

	public static UniversalCodec<Object> getDefaultEnumTypedCodec(AnnotatedElement annotatedElement, Type type, CodecRegistry registry) {
		DefaultEnumTypedCodec defaultCodec = annotatedElement.getAnnotation(DefaultEnumTypedCodec.class);
		if (defaultCodec == null) return null;
		Class<?> enumClass = defaultCodec.value();
		if (!(Enum.class.isAssignableFrom(enumClass) && ConfigEnumType.class.isAssignableFrom(enumClass))) {
			log.error("Invalid @DefaultJsonEnumTypedCodec on \"" + annotatedElement + "\"");
			return null;
		}
		return new TypedEnumAdapter<>(ReflectUtils.getRawType(type), AutoCast.cast(enumClass), registry);
	}


	@SuppressWarnings("ClassCanBeRecord")
	public static class CollectionSerializer implements UniversalSerializer<Collection<Object>> {

		final CodecRegistry registry;

		public CollectionSerializer(CodecRegistry registry) {
			this.registry = registry;
		}

		@Override
		public void write(Collection<Object> value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
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
			writer.endList();
		}

		@Override
		public CodecRegistry getRegistry() {
			return registry;
		}
	}

	@SuppressWarnings("ClassCanBeRecord")
	public static class MapSerializer implements UniversalSerializer<Map<Object, Object>> {

		final CodecRegistry registry;

		public MapSerializer(CodecRegistry registry) {
			this.registry = registry;
		}

		@Override
		public void write(Map<Object, Object> value, UniversalWriter writer) throws IOException {
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
		public CodecRegistry getRegistry() {
			return registry;
		}
	}

	public static class MapCodec extends AbstractCodec<Map<?, ?>> {

		final Supplier<Map<Object, Object>> constructor;
		final UniversalDeserializer<Object> keyDeserializer;
		final UniversalDeserializer<Object> valueDeserializer;
		final UniversalSerializer<Object> keySerializer;
		final UniversalSerializer<Object> valueSerializer;

		public MapCodec(Class<?> tClass, CodecRegistry registry) {
			Type[] parameters;
			Class<?> c = tClass;
			while (true) {
				Class<?> s = c.getSuperclass();
				if (s != null) {
					AnnotatedType annotatedSuperclass = c.getAnnotatedSuperclass();
					if (annotatedSuperclass instanceof AnnotatedParameterizedType annotatedParameterizedType) {
						Type type = annotatedParameterizedType.getType();
						if (type instanceof ParameterizedType parameterizedType) {
							try {
								if (Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
									parameters = parameterizedType.getActualTypeArguments();
									if (parameters.length == 2) break;
								}
							} catch (Exception ignored) {
							}
						}
					}
				} else {
					throw new IllegalArgumentException("Unable to create codec for non-canonical map declaration \"" + tClass + "\"");
				}
				c = s;
			}
			this(tClass, parameters, registry);
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		public MapCodec(Type tClass, UniversalCodec[] codec, Supplier<Map> constructor, CodecRegistry registry) {
			super(tClass, registry);
			this.keyDeserializer = codec[0];
			this.valueDeserializer = codec[1];
			this.keySerializer = codec[0];
			this.valueSerializer = codec[1];
			this.constructor = (Supplier<Map<Object, Object>>) (Supplier) constructor;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		public MapCodec(Class<?> tClass, Type[] parameters, CodecRegistry registry) {
			super(tClass, registry);
			//this.tClass = tClass;
			if (parameters.length != 2) {
				throw new IllegalArgumentException("Unable to create codec for non-canonical map declaration \"" + tClass.getName()
						+ " " + Arrays.toString(parameters) + "\"");
			}
			this.keyDeserializer = registry.getDeserializerIndirect(parameters[0]);
			this.valueDeserializer = registry.getDeserializerIndirect(parameters[1]);
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
		public void write(Map<?, ?> value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginObject();
			int size = value.size();
			if (size > 1) {
				writer.lineBreakEnable(true);
			}

			for (Map.Entry<?, ?> entry : value.entrySet()) {
				Object k = entry.getKey();
				Object v = entry.getValue();
				writer.writeName(keySerializer.valueAsKeyString(k));
				valueSerializer.write(v, writer);
			}
			writer.endObject();
		}

		@Override
		public Map<?, ?> read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					Map<Object, Object> map = constructor.get();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String name = reader.readName(); // TODO check
						Object key;
						try {
							key = keyDeserializer.stringKeyToValue(name);
						} catch (Exception ex) {
							//this.keyDeserializer.stringKeyToValue(name);
							throw new ParseException("Exception while read \"" + name + "\", " + keyDeserializer, ex);
						}
						Object value;
						try {
							value = valueDeserializer.read(reader);
						} catch (Exception ex) {
							throw new ParseException("Exception while read \"" + name + "\"", ex);
						}
						map.put(key, value);
					}
					reader.endObject();
					if (map instanceof PostDeserializeCall postDeserializeCall) {
						postDeserializeCall.postDeserialized();
					}
					return map;
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}
	}

	public static class CollectionCodec extends AbstractCodec<Collection<?>> {

		final Supplier<Collection<Object>> constructor;
		final UniversalDeserializer<Object> deserializer;
		final UniversalSerializer<Object> serializer;

		@SuppressWarnings({"unchecked"})
		public CollectionCodec(Class<?> tClass, CodecRegistry registry) {
			Type[] parameters;
			Class<?> c = tClass;
			while (true) {
				Class<?> s = c.getSuperclass();
				if (s != null) {
					AnnotatedType annotatedSuperclass = c.getAnnotatedSuperclass();
					if (annotatedSuperclass instanceof AnnotatedParameterizedType annotatedParameterizedType) {
						Type type = annotatedParameterizedType.getType();
						if (type instanceof ParameterizedType parameterizedType) {
							try {
								if (Collection.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
									parameters = parameterizedType.getActualTypeArguments();
									if (parameters.length == 1) break;
								}
							} catch (Exception ignored) {
							}
						}
					}
				} else {
					throw new IllegalArgumentException("Unable to create codec for non-canonical collection declaration \"" + tClass + "\"");
				}
				c = s;
			}
			Supplier<Collection<Object>> tmpC;
			if (tClass.isInterface() || Modifier.isAbstract(tClass.getModifiers())) {
				throw new IllegalArgumentException("Unable to create codec for abstract class \"" + tClass + "\"");
			} else {
				tmpC = (Supplier<Collection<Object>>) ReflectUtils.getConstructor(tClass);
				if (tmpC == null) {
					throw new IllegalArgumentException("Class \"" + tClass.getName() + "\" have no empty constructor!");
				}
			}
			this(tClass, parameters, tmpC, registry);
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		public CollectionCodec(Type tClass, UniversalCodec<?> codec, Supplier<Collection<?>> defaultSupplier, CodecRegistry registry) {
			super(tClass, registry);
			this.deserializer = (UniversalDeserializer<Object>) codec;
			this.serializer = (UniversalSerializer<Object>) codec;
			this.constructor = (Supplier<Collection<Object>>) (Supplier) defaultSupplier;
		}

		@SuppressWarnings({"unchecked", "rawtypes"})
		public CollectionCodec(Class<?> tClass, Type[] parameters, Supplier<Collection<Object>> defaultSupplier, CodecRegistry registry) {
			super(tClass, registry);
			this.deserializer = registry.getDeserializerIndirect(parameters[0]);
			this.serializer = getUniversalSerializer(parameters[0], registry);
			Supplier<Collection<Object>> tmpC;
			if (tClass.isInterface() || Modifier.isAbstract(tClass.getModifiers())) {
				if (EnumSet.class.isAssignableFrom(tClass)) {
					tmpC = () -> EnumSet.noneOf(((Class<? extends Enum>) parameters[0]));
				} else {
					tmpC = defaultSupplier;
				}
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
		public void write(Collection<?> value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			int size = value.size();
			if (size > 1) {
				writer.lineBreakEnable(true);
			}
			for (Object v : value) {
				serializer.write(v, writer);
			}
			writer.endList();
		}

		@Override
		public Collection<Object> read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					Collection<Object> list = constructor.get();
					int i = 0;
					while (reader.nextEntryType() != SosisonEntryType.END_LIST) {
						try {
							list.add(deserializer.read(reader));
						} catch (Exception ex) {
							throw new ParseException("Exception while read [" + i + "]", ex);
						}
						i++;
					}
					reader.endList();
					if (list instanceof PostDeserializeCall postDeserializeCall) {
						postDeserializeCall.postDeserialized();
					}
					return list;
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}
	}

	public static class ArrayCodec extends AbstractCodec<Object> {

		final Class<?> tClass;
		final UniversalDeserializer<Object> deserializer;
		final UniversalSerializer<Object> serializer;
		final Object[] array;

		public ArrayCodec(Class<?> type, CodecRegistry registry) {
			this(type, registry.getDeserializerIndirect(type), registry.getSerializerIndirect(type), registry);
		}

		public ArrayCodec(Class<?> tClass, UniversalDeserializer<Object> deserializer, UniversalSerializer<Object> serializer, CodecRegistry registry) {
			super(tClass, registry);
			this.tClass = tClass;
			this.deserializer = deserializer;
			this.serializer = serializer;
			this.array = (Object[]) Array.newInstance(tClass, 0);
		}

		@Override
		public void write(Object value, UniversalWriter writer) throws IOException {
			write(value, writer, this.serializer);
		}

		@SuppressWarnings("unchecked")
		public static <T> void write(Object value, UniversalWriter writer, UniversalSerializer<T> serializer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			int size = Array.getLength(value);
			if (size > 1) {
				writer.lineBreakEnable(true);
			}
			for (int i = 0; i < size; i++) {
				T v = (T) Array.get(value, i);
				serializer.write(v, writer);
			}
			writer.endList();
		}

		@Override
		public Object read(UniversalReader reader) throws IOException {
			return read(this.array, reader, this.deserializer);
		}

		public static <T> T[] read(T[] emptyArray, UniversalReader reader, UniversalDeserializer<T> deserializer) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					ArrayList<Object> list = new ArrayList<>();
					int i = 0;
					while (reader.nextEntryType() != SosisonEntryType.END_LIST) {
						try {
							list.add(deserializer.read(reader));
						} catch (Exception ex) {
							throw new ParseException("Exception while read [" + i + "]", ex);
						}
						i++;
					}
					reader.endList();
					return list.toArray(emptyArray);
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}
	}

	public static final class IntArrayCodec extends AbstractCodec<int[]> {

		public IntArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(int[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeIntArray(value);
		}

		@Override
		public int[] read(UniversalReader reader) throws IOException {
			return reader.readIntArray();
		}
	}

	public static final class ByteArrayCodec extends AbstractCodec<byte[]> {

		public ByteArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(byte[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeByteArray(value);
		}

		@Override
		public byte[] read(UniversalReader reader) throws IOException {
			return reader.readByteArray();
		}
	}

	public static final class BooleanArrayCodec extends AbstractCodec<boolean[]> {

		public BooleanArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(boolean[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			int size = value.length;
			for (int i = 0; i < size; i++) {
				writer.writeBoolean(value[i]);
			}
			writer.endList();
		}

		@Override
		public boolean[] read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					ArrayUtils.BooleanGrowingArray array = new ArrayUtils.BooleanGrowingArray(16);
					while (reader.nextEntryType() != SosisonEntryType.END_LIST) {
						array.add(reader.readBoolean());
					}
					reader.endList();
					return array.getArray();
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}
	}

	public static final class ShortArrayCodec extends AbstractCodec<short[]> {

		public ShortArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(short[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeShortArray(value);
		}

		@Override
		public short[] read(UniversalReader reader) throws IOException {
			return reader.readShortArray();
		}
	}

	public static final class CharArrayCodec extends AbstractCodec<char[]> {

		public CharArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(char[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeCharArray(value);
		}

		@Override
		public char[] read(UniversalReader reader) throws IOException {
			return reader.readCharArray();
		}
	}

	public static final class LongArrayCodec extends AbstractCodec<long[]> {

		public LongArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(long[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeLongArray(value);
		}

		@Override
		public long[] read(UniversalReader reader) throws IOException {
			return reader.readLongArray();
		}
	}


	public static final class FloatArrayCodec extends AbstractCodec<float[]> {

		public FloatArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(float[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeFloatArray(value);
		}

		@Override
		public float[] read(UniversalReader reader) throws IOException {
			return reader.readFloatArray();
		}
	}

	public static final class DoubleArrayCodec extends AbstractCodec<double[]> {

		public DoubleArrayCodec(CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(double[] value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeDoubleArray(value);
		}

		@Override
		public double[] read(UniversalReader reader) throws IOException {
			return reader.readDoubleArray();
		}
	}


	public static final class FileCodec extends AbstractCodec<File> {

		public FileCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public File read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					URI uri = URI.create(reader.readString().replace(" ", "%20"));
					if (uri.getScheme() == null) {
						return new File(uri.getPath());
					}
					return new File(uri);
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}

		@Override
		public void write(File value, UniversalWriter writer) throws IOException {
			URI uri = value.toURI();
			if (uri.getScheme() == null || uri.getScheme().equals("file")) {
				writer.writeString(uri.toString().replace("\\", "/"));
				return;
			}
			writer.writeString(uri.toString());
		}
	}


	public static final class PathCodec extends AbstractCodec<Path> {

		public PathCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Path read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					URI uri = URI.create(SKDSFiles.toCanonicalPath(reader.readString()));
					if (uri.getScheme() == null) {
						return Path.of(uri.getPath());
					}
					return Path.of(uri);
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}

		@Override
		public void write(Path value, UniversalWriter writer) throws IOException {
			URI uri = value.toUri();
			if (uri.getScheme() == null || uri.getScheme().equals("file")) {
				writer.writeString(uri.toString().replace("\\", "/"));
				return;
			}
			writer.writeString(uri.toString());
		}
	}

	public static final class URICodec extends AbstractCodec<URI> {

		public URICodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public URI read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					return URI.create(reader.readString());
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}

		@Override
		public void write(URI value, UniversalWriter writer) throws IOException {
			writer.writeString(value.toString());
		}
	}

	public static final class URLCodec extends AbstractCodec<URL> {

		public URLCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public URL read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					return URI.create(reader.readString()).toURL();
				}
				default -> throw new ParseException("Unexpected token " + type);
			}
		}

		@Override
		public void write(URL value, UniversalWriter writer) throws IOException {
			writer.writeString(value.toString());
		}
	}

	public static final class UUIDCodec extends AbstractCodec<UUID> {

		public UUIDCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public UUID stringKeyToValue(String key) throws IOException {
			return super.stringKeyToValue(key);
		}

		@Override
		public UUID read(UniversalReader reader) throws IOException {
			return reader.readUUID();
		}

		@Override
		public void write(UUID value, UniversalWriter writer) throws IOException {
			writer.writeUUID(value);
		}
	}

	public static final class ObjectCodec extends AbstractCodec<Object> {

		final UniversalDeserializer<JsonObject> jod;
		final UniversalDeserializer<JsonArray> jad;

		public ObjectCodec(Type type, CodecRegistry registry) {
			super(registry);
			this.jod = registry.getDeserializerIndirect(JsonObject.class);
			this.jad = registry.getDeserializerIndirect(JsonArray.class);
		}

		@Override
		public Object read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case STRING -> {
					return reader.readString();
				}
				case BOOLEAN -> {
					return reader.readBoolean();
				}
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_OBJECT -> {
					return jod.read(reader);
				}
				case BEGIN_LIST -> {
					return jad.read(reader);
				}
				default -> {
					if (type.isNumber()) {
						return reader.readNumber();
					}
					throw new ParseException("Unexpected token " + type);
				}
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public void write(Object value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			Class<Object> cl = (Class<Object>) value.getClass();
			if (cl == Object.class) {
				writer.beginObject();
				writer.endObject();
				return;
			}
			UniversalSerializer<Object> ser = registry.getSerializer(cl);
			ser.write(value, writer);
		}
	}

	public static final class StringCodec extends AbstractCodec<String> {

		final UniversalDeserializer<JsonObject> jod;
		final UniversalDeserializer<JsonArray> jad;

		public StringCodec(Type type, CodecRegistry registry) {
			super(registry);
			this.jod = registry.getDeserializerIndirect(JsonObject.class);
			this.jad = registry.getDeserializerIndirect(JsonArray.class);
		}

		@Override
		public String stringKeyToValue(String key) throws IOException {
			return key;
		}

		@Override
		public String valueAsKeyString(String val) {
			return val;
		}

		@Override
		public void write(String value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeString(value);
		}

		@Override
		public String read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					return reader.readString();
				}
				case BOOLEAN -> {
					return String.valueOf(reader.readBoolean());
				}
				case BEGIN_OBJECT -> {
					return jod.read(reader).toString();    // TODO
				}
				case BEGIN_LIST -> {
					return jad.read(reader).toString();    // TODO
				}
				default -> {
					if (type.isNumber()) {
						return String.valueOf(reader.readNumber());
					}
					throw new ParseException("Unexpected token " + type);
				}
			}
		}
	}

	public static final class DateCodec extends AbstractCodec<Date> {

		public DateCodec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public Date read(UniversalReader reader) throws IOException {
			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case LONG -> {
					return new Time(reader.readLong());
				}
				case STRING -> {
					String s = reader.readString();
					return Date.from(Instant.parse(s));
				}
			}
			throw new ParseException("Unable to parse date");
		}

		@Override
		public void write(Date value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
			} else {
				writer.writeString(value.toInstant().toString());
			}
		}
	}

	public static final class ColorCodec extends AbstractCodec<Color> {

		public ColorCodec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public String valueAsKeyString(Color val) {
			return String.valueOf(val.getRGB());
		}

		@Override
		public Color stringKeyToValue(String key) throws IOException {
			return stringKeyToCompositeValue(key);
		}

		@Override
		public void write(Color value, UniversalWriter writer) throws IOException {
			if (value != null) {
				writer.writeHex(value.getRGB());
			}
		}

		@Override
		public Color read(UniversalReader reader) throws IOException {
			var type = reader.nextEntryType();
			return switch (type) {
				case NULL -> null;
				case STRING -> new Color(reader.readInt(), true);
				case BEGIN_LIST -> {
					reader.beginList();
					int r = reader.readInt();
					int g = reader.readInt();
					int b = reader.readInt();
					int a = 255;
					if (reader.nextEntryType() != SosisonEntryType.END_LIST) {
						a = reader.readInt();
					}
					Color color = new Color(r, g, b, a);
					reader.endList();
					yield color;
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					int r = 255;
					int g = 255;
					int b = 255;
					int a = 255;
					while (reader.nextEntryType() != END_OBJECT) {
						String s = reader.readName();
						int i = reader.readInt();
						switch (s.toLowerCase()) {
							case "r" -> r = i;
							case "g" -> g = i;
							case "b" -> b = i;
							case "a" -> a = i;
						}
					}
					reader.endObject();
					yield new Color(r, g, b, a);
				}
				default -> {
					if (type.isNumber()) {
						yield new Color(reader.readInt(), true);
					}
					throw new ParseException("Unexpected token " + type);
				}
			};
		}
	}

	public static final class EnumCodec<E extends Enum<E>> extends AbstractCodec<E> {

		private final Class<E> eClass;

		@SuppressWarnings("unchecked")
		public EnumCodec(Type type, CodecRegistry registry) {
			super(registry);
			eClass = (Class<E>) type;
		}

		@Override
		public String valueAsKeyString(E val) {
			return val.name();
		}

		@Override
		public void write(E value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeString(value.name());
		}

		@Override
		public E read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					return parseEnum(reader.readString(), eClass);
				}
				default -> {
					if (type.isNumber()) {
						int n = reader.readInt();
						E[] values = eClass.getEnumConstants();
						if (n < 0 || n >= values.length) return null;
						return values[n];
					} else
						throw new ParseException("Unexpected token " + type);
				}
			}
		}
	}

	public static <E extends Enum<E>> E parseEnum(String value, Class<E> type) {
		return parseEnum(value, type, false);
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E parseEnum(String value, Class<E> type, boolean throwException) {
		try {
			try {
				return Enum.valueOf(type, value);
			} catch (IllegalArgumentException | NullPointerException e) {
				EnumNameDataFixer annotation = type.getAnnotation(EnumNameDataFixer.class);
				if (annotation != null) {
					IEnumNameDataFixer<E> fixer = (IEnumNameDataFixer<E>) ReflectUtils.getConstructor(annotation.value()).get();
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

	public static final class IntCodec extends AbstractCodec<Integer> {

		public IntCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Integer stringKeyToValue(String key) throws IOException {
			return key != null ? Integer.parseInt(key) : null;
		}

		@Override
		public void write(Integer value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeInt(0);
				return;
			}
			writer.writeRaw(value.toString());
		}

		@Override
		public Integer read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			return reader.readInt();
		}
	}

	public static final class WrappedIntCodec extends AbstractCodec<Integer> {

		public WrappedIntCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Integer stringKeyToValue(String key) throws IOException {
			return key != null ? Integer.parseInt(key) : 0;
		}

		@Override
		public void write(Integer value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Integer read(UniversalReader reader) throws IOException {
			try {
				reader.nextEntryType();
			} catch (Exception e) {
				reader.nextEntryType();
			}
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readInt();
		}
	}

	public static final class ByteCodec extends AbstractCodec<Byte> {

		public ByteCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Byte value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeByte((byte) 0);
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Byte read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			return (byte) reader.readInt();
		}
	}

	public static final class WrappedByteCodec extends AbstractCodec<Byte> {

		public WrappedByteCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Byte value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Byte read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return (byte) reader.readInt();
		}
	}

	public static final class NumberCodec extends AbstractCodec<Number> {

		public NumberCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Number stringKeyToValue(String key) throws IOException {
			return key != null ? Numbers.parseNumber(key) : null;
		}

		@Override
		public void write(Number value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeRaw(value.toString());
		}

		@Override
		public Number read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readNumber();
		}
	}

	public static final class BooleanCodec extends AbstractCodec<Boolean> {

		public BooleanCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Boolean stringKeyToValue(String key) throws IOException {
			return Boolean.parseBoolean(key);
		}

		@Override
		public void write(Boolean value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeBoolean(false);
				return;
			}
			writer.writeBoolean(value);
		}

		@Override
		public Boolean read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return false;
			}
			return reader.readBoolean();
		}
	}

	public static final class WrappedBooleanCodec extends AbstractCodec<Boolean> {

		public WrappedBooleanCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Boolean stringKeyToValue(String key) throws IOException {
			return key != null ? Boolean.parseBoolean(key) : null;
		}

		@Override
		public void write(Boolean value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeBoolean(value);
		}

		@Override
		public Boolean read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readBoolean();
		}
	}

	public static final class CharCodec extends AbstractCodec<Character> {

		public CharCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Character value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeString(StringUtils.unicodeCharUC((char) 0));
				return;
			}
			writer.writeString(StringUtils.unicodeCharUC(value));
		}

		@Override
		public Character read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0;
				}
				case STRING -> {
					String cs = reader.readString();
					if (cs.length() == 1) {
						return cs.charAt(0);
					} else {
						throw new ParseException("Unexpected char " + cs);
					}
				}
				default -> {
					if (type.isNumber()) {
						return (char) reader.readInt();
					}
					throw new ParseException("Unexpected token " + type);
				}
			}
		}
	}

	public static final class WrappedCharCodec extends AbstractCodec<Character> {

		public WrappedCharCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Character value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeString(StringUtils.unicodeCharUC(value));
		}

		@Override
		public Character read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case STRING -> {
					String cs = reader.readString();
					if (cs.length() == 1) {
						return cs.charAt(0);
					} else {
						throw new ParseException("Unexpected char " + cs);
					}
				}
				default -> {
					if (type.isNumber()) {
						return (char) reader.readInt();
					}
					throw new ParseException("Unexpected token " + type);
				}
			}
		}
	}

	public static final class ShortCodec extends AbstractCodec<Short> {

		public ShortCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Short stringKeyToValue(String key) throws IOException {
			return key != null ? Short.valueOf(key) : null;
		}

		@Override
		public void write(Short value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeShort((short) 0);
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Short read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			return (short) reader.readInt();
		}
	}

	public static final class WrappedShortCodec extends AbstractCodec<Short> {

		public WrappedShortCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Short stringKeyToValue(String key) throws IOException {
			return key != null ? Short.valueOf(key) : 0;
		}

		@Override
		public void write(Short value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeInt(value);
		}

		@Override
		public Short read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return 0;
			}
			return (short) reader.readInt();
		}
	}

	public static final class LongCodec extends AbstractCodec<Long> {

		public LongCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Long stringKeyToValue(String key) throws IOException {
			return key != null ? Long.parseLong(key) : null;
		}

		@Override
		public void write(Long value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeLong(0);
				return;
			}
			writer.writeLong(value);
		}

		@Override
		public Long read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return 0L;
			}
			return reader.readLong();
		}
	}

	public static final class WrappedLongCodec extends AbstractCodec<Long> {

		public WrappedLongCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Long stringKeyToValue(String key) throws IOException {
			return key != null ? Long.parseLong(key) : null;
		}

		@Override
		public void write(Long value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeLong(value);
		}

		@Override
		public Long read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readLong();
		}
	}

	public static final class FloatCodec extends AbstractCodec<Float> {

		public FloatCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Float stringKeyToValue(String key) throws IOException {
			return key != null ? Float.parseFloat(key) : null;
		}

		@Override
		public void write(Float value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeFloat(0);
				return;
			}
			writer.writeFloat(value);
		}

		@Override
		public Float read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return 0f;
			}
			return reader.readFloat();
		}
	}

	public static final class WrappedFloatCodec extends AbstractCodec<Float> {

		public WrappedFloatCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Float stringKeyToValue(String key) throws IOException {
			return key != null ? Float.parseFloat(key) : null;
		}

		@Override
		public void write(Float value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeFloat(value);
		}

		@Override
		public Float read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readFloat();
		}
	}

	public static final class DoubleCodec extends AbstractCodec<Double> {

		public DoubleCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Double stringKeyToValue(String key) throws IOException {
			return key != null ? Double.parseDouble(key) : null;
		}

		@Override
		public void write(Double value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeDouble(0);
				return;
			}
			writer.writeDouble(value);
		}

		@Override
		public Double read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return 0d;
			}
			return reader.readDouble();
		}
	}

	public static final class WrappedDoubleCodec extends AbstractCodec<Double> {

		public WrappedDoubleCodec(Type type, CodecRegistry registry) {
			super(registry);
		}

		@Override
		public Double stringKeyToValue(String key) throws IOException {
			return key != null ? Double.parseDouble(key) : null;
		}

		@Override
		public void write(Double value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.writeDouble(value);
		}

		@Override
		public Double read(UniversalReader reader) throws IOException {
			SosisonEntryType type = reader.nextEntryType();
			if (type == SosisonEntryType.NULL) {
				reader.skipNull();
				return null;
			}
			return reader.readDouble();
		}
	}
}
