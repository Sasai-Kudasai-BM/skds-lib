package net.skds.lib2.mat.vec4;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
@DefaultCodec(Vec4D.Codec.class)
public record Vec4D(double x, double y, double z, double w) implements Vec4 {

	public static final Vec4D XN = new Vec4D(-1.0D, 0.0D, 0.0D, 0.0D);
	public static final Vec4D XP = new Vec4D(1.0D, 0.0D, 0.0D, 0.0D);
	public static final Vec4D YN = new Vec4D(0.0D, -1.0D, 0.0D, 0.0D);
	public static final Vec4D YP = new Vec4D(0.0D, 1.0D, 0.0D, 0.0D);
	public static final Vec4D ZN = new Vec4D(0.0D, 0.0D, -1.0D, 0.0D);
	public static final Vec4D ZP = new Vec4D(0.0D, 0.0D, 1.0D, 0.0D);
	public static final Vec4D WN = new Vec4D(0.0D, 0.0D, 0.0D, -1.0D);
	public static final Vec4D WP = new Vec4D(0.0D, 0.0D, 0.0D, 1.0D);
	public static final Vec4D SINGLE = new Vec4D(1.0D, 1.0D, 1.0D, 1.0D);
	public static final Vec4D ZERO = new Vec4D(0.0D, 0.0D, 0.0D, 0.0D);

	public Vec4D(Vec4 vec) {
		this(vec.x(), vec.y(), vec.z(), vec.w());
	}

	public Vec4D(double size) {
		this(size, size, size, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof Vec4 vec && switch (vec) {
			case Vec4D(double x2, double y2, double z2, double w2) ->
					this.x == x2 && this.y == y2 && this.z == z2 && this.w == w2;
			case Vec4F(float x2, float y2, float z2, float w2) ->
					this.x == x2 && this.y == y2 && this.z == z2 && this.w == w2;
			case Vec4I(int x2, int y2, int z2, int w2) -> this.x == x2 && this.y == y2 && this.z == z2 && this.w == w2;
		};
	}

	@Override
	public int hashCode() {
		return Vec4.hashCode(this);
	}

	@Override
	public Vec4D getAsDoubleVec() {
		return this;
	}

	static final class Codec extends AbstractCodec<Vec4> {

		public Codec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public String valueAsKeyString(Vec4 val) {
			if (val == null) {
				return null;
			}
			return val.getAsArrayString();
		}

		@Override
		public Vec4 stringKeyToValue(String key) throws IOException {
			return stringKeyToCompositeValue(key);
		}

		@Override
		public void write(Vec4 value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			writer.writeDouble(value.x());
			writer.writeDouble(value.y());
			writer.writeDouble(value.z());
			writer.writeDouble(value.w());
			writer.endList();
		}

		@Override
		public Vec4D read(UniversalReader reader) throws IOException {
			double x = 0;
			double y = 0;
			double z = 0;
			double w = 0;

			return switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					yield null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readDouble();
					y = reader.readDouble();
					z = reader.readDouble();
					w = reader.readDouble();
					reader.endList();
					yield new Vec4D(x, y, z, w);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x" -> x = reader.readDouble();
							case "y" -> y = reader.readDouble();
							case "z" -> z = reader.readDouble();
							case "w" -> w = reader.readDouble();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec4D(x, y, z, w);
				}
				case INT -> new Vec4D(reader.readInt());
				case FLOAT -> new Vec4D(reader.readFloat());
				case LONG -> new Vec4D(reader.readLong());
				case DOUBLE -> new Vec4D(reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}
