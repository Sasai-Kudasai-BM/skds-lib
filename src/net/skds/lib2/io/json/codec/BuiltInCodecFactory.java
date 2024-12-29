package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.elements.*;
import net.skds.lib2.utils.ArrayUtils;
import net.skds.lib2.utils.StringUtils;
import net.skds.lib2.utils.collection.ImmutableArrayHashMap;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

class BuiltInCodecFactory implements JsonCodecFactory {

	final Map<Type, JsonCodecFactory> map = new ImmutableArrayHashMap<>(
			JsonObject.class, (JsonCodecFactory) JsonObject.Codec::new,
			JsonElement.class, (JsonCodecFactory) JsonElement.Codec::new,
			JsonString.class, (JsonCodecFactory) JsonString.Codec::new,
			JsonNumber.class, (JsonCodecFactory) JsonNumber.Codec::new,
			JsonBoolean.class, (JsonCodecFactory) JsonBoolean.Codec::new,
			JsonArray.class, (JsonCodecFactory) JsonArray.Codec::new,

			String.class, (JsonCodecFactory) StringCodec::new,

			Byte.class, (JsonCodecFactory) ByteCodec::new,
			Boolean.class, (JsonCodecFactory) BooleanCodec::new,
			Short.class, (JsonCodecFactory) ShortCodec::new,
			Character.class, (JsonCodecFactory) CharCodec::new,
			Integer.class, (JsonCodecFactory) IntCodec::new,
			Long.class, (JsonCodecFactory) LongCodec::new,
			Float.class, (JsonCodecFactory) FloatCodec::new,
			Double.class, (JsonCodecFactory) DoubleCodec::new,

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
	@SuppressWarnings("unchecked")
	public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
		if (type instanceof Class<?> cl) {
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
				} else if (cle.isArray()) {
					JsonCodec<Object> codec = (JsonCodec<Object>) createCodec(cle, registry);
					return new ArrayCodec(cle, codec, registry);
				} else {
					JsonCodec<Object> codec = registry.getCodec((Type) cle);
					return new ArrayCodec(cle, codec, registry);
				}
			}
		}
		JsonCodecFactory fac = map.get(type);
		return fac == null ? null : fac.createCodec(type, registry);
	}

	public static final class ArrayCodec extends JsonCodec<Object> {

		final Class<?> tClass;
		final JsonCodec<Object> elementCodec;
		final Object[] array;

		public ArrayCodec(Class<?> tClass, JsonCodec<Object> elementCodec, JsonCodecRegistry registry) {
			super(registry);
			this.tClass = tClass;
			this.elementCodec = elementCodec;
			this.array = (Object[]) Array.newInstance(tClass, 0);
		}

		@Override
		public void write(Object value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			int size = Array.getLength(value);
			if (size > 1) {
				writer.setLineBreakEnable(true);
			}
			for (int i = 0; i < size; i++) {
				elementCodec.write(Array.get(value, i), writer);
			}
			writer.endArray();
		}

		@Override
		public Object read(JsonReader reader) throws IOException {
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
						list.add(elementCodec.read(reader));
					}
					reader.endArray();
					return list.toArray(array);
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class IntArrayCodec extends JsonCodec<int[]> {

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

	public static final class ByteArrayCodec extends JsonCodec<byte[]> {

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

	public static final class BooleanArrayCodec extends JsonCodec<boolean[]> {

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

	public static final class ShortArrayCodec extends JsonCodec<short[]> {

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

	public static final class CharArrayCodec extends JsonCodec<char[]> {

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

	public static final class LongArrayCodec extends JsonCodec<long[]> {

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


	public static final class FloatArrayCodec extends JsonCodec<float[]> {

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

	public static final class DoubleArrayCodec extends JsonCodec<double[]> {

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


	public static final class StringCodec extends JsonCodec<String> {

		final JsonCodec<JsonObject> joc;
		final JsonCodec<JsonArray> jac;

		public StringCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
			this.joc = registry.getCodec(JsonObject.class);
			this.jac = registry.getCodec(JsonArray.class);
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
					return joc.read(reader).toString();    // TODO
				}
				case BEGIN_ARRAY -> {
					return jac.read(reader).toString();    // TODO
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class EnumCodec<E extends Enum<E>> extends JsonCodec<E> {

		private final Class<E> eClass;

		@SuppressWarnings("unchecked")
		public EnumCodec(Type type, JsonCodecRegistry registry) {
			super(registry);
			eClass = (Class<E>) type;
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

	public static final class IntCodec extends JsonCodec<Integer> {

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
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return n.intValue();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class ByteCodec extends JsonCodec<Byte> {

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
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return n.byteValue();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}


	public static final class BooleanCodec extends JsonCodec<Boolean> {

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
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return false;
				}
				case NUMBER -> {
					return reader.readBoolean();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class CharCodec extends JsonCodec<Character> {

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

	public static final class ShortCodec extends JsonCodec<Short> {

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
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return n.shortValue();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class LongCodec extends JsonCodec<Long> {

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
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0L;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return n.longValue();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class FloatCodec extends JsonCodec<Float> {

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
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0f;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return n.floatValue();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}

	public static final class DoubleCodec extends JsonCodec<Double> {

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
			switch (type) {
				case NULL -> {
					reader.skipNull();
					return 0d;
				}
				case NUMBER -> {
					Number n = reader.readNumber();
					return n.doubleValue();
				}
				default -> throw new JsonReadException("Unexpected token " + type);
			}
		}
	}
}