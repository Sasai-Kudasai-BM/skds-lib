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
@DefaultCodec(Vec4F.Codec.class)
public record Vec4F(float xf, float yf, float zf, float wf) implements Vec4 {

	public static final Vec4F XN = new Vec4F(-1.0F, 0.0F, 0.0F, 0.0F);
	public static final Vec4F XP = new Vec4F(1.0F, 0.0F, 0.0F, 0.0F);
	public static final Vec4F YN = new Vec4F(0.0F, -1.0F, 0.0F, 0.0F);
	public static final Vec4F YP = new Vec4F(0.0F, 1.0F, 0.0F, 0.0F);
	public static final Vec4F ZN = new Vec4F(0.0F, 0.0F, -1.0F, 0.0F);
	public static final Vec4F ZP = new Vec4F(0.0F, 0.0F, 1.0F, 0.0F);
	public static final Vec4F WN = new Vec4F(0.0F, 0.0F, 0.0F, -1.0F);
	public static final Vec4F WP = new Vec4F(0.0F, 0.0F, 0.0F, 1.0F);
	public static final Vec4F SINGLE = new Vec4F(1.0F, 1.0F, 1.0F, 1.0F);
	public static final Vec4F ZERO = new Vec4F(0.0F, 0.0F, 0.0F, 0.0F);

	@Override
	public double x() {
		return xf;
	}

	@Override
	public double y() {
		return yf;
	}

	@Override
	public double z() {
		return zf;
	}

	@Override
	public double w() {
		return wf;
	}

	@Override
	public int xi() {
		return (int) xf;
	}

	@Override
	public int yi() {
		return (int) yf;
	}

	@Override
	public int zi() {
		return (int) zf;
	}

	@Override
	public int wi() {
		return (int) wf;
	}

	public Vec4F(Vec4 vec) {
		this(vec.xf(), vec.yf(), vec.zf(), vec.wf());
	}

	public Vec4F(float size) {
		this(size, size, size, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof Vec4 vec && switch (vec) {
			case Vec4F(float x2, float y2, float z2, float w2) ->
					this.xf == x2 && this.yf == y2 && this.zf == z2 && this.wf == w2;

			case Vec4D(double x2, double y2, double z2, double w2) ->
					(double) this.xf == x2 && (double) this.yf == y2 && (double) this.zf == z2 && (double) this.wf == w2;
			case Vec4I(int x2, int y2, int z2, int w2) ->
					(double) this.xf == (double) x2 && (double) this.yf == (double) y2 && (double) this.zf == (double) z2 && (double) this.wf == (double) w2;
		};
	}

	@Override
	public int hashCode() {
		return Vec4.hashCode(this);
	}

	@Override
	public Vec4F getAsFloatVec() {
		return this;
	}

	@Override
	public String getAsArrayString() {
		return "[" + this.xf() + "," + this.yf() + "," + this.zf() + "," + this.wf() + "]";
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
			writer.writeFloat(value.xf());
			writer.writeFloat(value.yf());
			writer.writeFloat(value.zf());
			writer.writeFloat(value.wf());
			writer.endList();
		}

		@Override
		public Vec4F read(UniversalReader reader) throws IOException {
			float x = 0;
			float y = 0;
			float z = 0;
			float w = 0;

			return switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					yield null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readFloat();
					y = reader.readFloat();
					z = reader.readFloat();
					w = reader.readFloat();
					reader.endList();
					yield new Vec4F(x, y, z, w);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x" -> x = reader.readFloat();
							case "y" -> y = reader.readFloat();
							case "z" -> z = reader.readFloat();
							case "w" -> w = reader.readFloat();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec4F(x, y, z, w);
				}
				case INT -> new Vec4F(reader.readInt());
				case FLOAT -> new Vec4F(reader.readFloat());
				case LONG -> new Vec4F(reader.readLong());
				case DOUBLE -> new Vec4F((float) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}
