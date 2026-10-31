package net.skds.lib2.mat.vec4;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.mat.FastMath;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.skds.lib2.io.sosison.SosisonEntryType.END_OBJECT;

@SuppressWarnings("unused")
@DefaultCodec(Vec4I.Codec.class)
public record Vec4I(int xi, int yi, int zi, int wi) implements Vec4, Comparable<Vec4> {

	public static final Vec4I XN = new Vec4I(-1, 0, 0, 0);
	public static final Vec4I XP = new Vec4I(1, 0, 0, 0);
	public static final Vec4I YN = new Vec4I(0, -1, 0, 0);
	public static final Vec4I YP = new Vec4I(0, 1, 0, 0);
	public static final Vec4I ZN = new Vec4I(0, 0, -1, 0);
	public static final Vec4I ZP = new Vec4I(0, 0, 1, 0);
	public static final Vec4I WN = new Vec4I(0, 0, 0, -1);
	public static final Vec4I WP = new Vec4I(0, 0, 0, 1);
	public static final Vec4I SINGLE = new Vec4I(1, 1, 1, 1);
	public static final Vec4I ZERO = new Vec4I(0, 0, 0, 0);

	@Override
	public double x() {
		return xi;
	}

	@Override
	public double y() {
		return yi;
	}

	@Override
	public double z() {
		return zi;
	}

	@Override
	public double w() {
		return wi;
	}

	@Override
	public float xf() {
		return xi;
	}

	@Override
	public float yf() {
		return yi;
	}

	@Override
	public float zf() {
		return zi;
	}

	@Override
	public float wf() {
		return wi;
	}

	@Override
	public int floorX() {
		return xi;
	}

	@Override
	public int floorY() {
		return yi;
	}

	@Override
	public int floorZ() {
		return zi;
	}

	@Override
	public int floorW() {
		return wi;
	}

	@Override
	public int ceilX() {
		return xi;
	}

	@Override
	public int ceilY() {
		return yi;
	}

	@Override
	public int ceilZ() {
		return zi;
	}

	@Override
	public int ceilW() {
		return wi;
	}

	@Override
	public int roundX() {
		return xi;
	}

	@Override
	public int roundY() {
		return yi;
	}

	@Override
	public int roundZ() {
		return zi;
	}

	@Override
	public int roundW() {
		return wi;
	}

	public Vec4I(Vec4 vec) {
		this(vec.floorX(), vec.floorY(), vec.floorZ(), vec.floorW());
	}

	public Vec4I(int size) {
		this(size, size, size, size);
	}

	public Vec4I(double x, double y, double z, double w) {
		this(FastMath.floor(x), FastMath.floor(y), FastMath.floor(z), FastMath.floor(w));
	}

	public Vec4 getCenter() {
		return Vec4.of(this.xi() + 0.5, this.yi() + 0.5, this.zi() + 0.5, this.wi() + 0.5);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof Vec4 vec && switch (vec) {
			case Vec4I(int x2, int y2, int z2, int w2) ->
					this.xi == x2 && this.yi == y2 && this.zi == z2 && this.wi == w2;

			case Vec4D(double x2, double y2, double z2, double w2) ->
					(double) this.xi == x2 && (double) this.yi == y2 && (double) this.zi == z2 && (double) this.wi == w2;
			case Vec4F(float x2, float y2, float z2, float w2) ->
					(double) this.xi == (double) x2 && (double) this.yi == (double) y2 && (double) this.zi == (double) z2 && (double) this.wi == (double) w2;
		};
	}

	@Override
	public int hashCode() {
		return Vec4.hashCode(this);
	}

	@Override
	public Vec4I getAsIntVec() {
		return this;
	}

	@Override
	public String getAsArrayString() {
		return "[" + this.xi() + "," + this.yi() + "," + this.zi() + "," + this.wi() + "]";
	}

	@Override
	public int compareTo(Vec4 o) {
		return Vec4.compare(this, o);
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
			writer.writeLong(value.xi());
			writer.writeLong(value.yi());
			writer.writeLong(value.zi());
			writer.writeLong(value.wi());
			writer.endList();
		}

		@Override
		public Vec4I read(UniversalReader reader) throws IOException {
			int x = 0;
			int y = 0;
			int z = 0;
			int w = 0;

			return switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					yield null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readInt();
					y = reader.readInt();
					z = reader.readInt();
					w = reader.readInt();
					reader.endList();
					yield new Vec4I(x, y, z, w);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x" -> x = reader.readInt();
							case "y" -> y = reader.readInt();
							case "z" -> z = reader.readInt();
							case "w" -> w = reader.readInt();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec4I(x, y, z, w);
				}
				case INT -> new Vec4I(reader.readInt());
				case FLOAT -> new Vec4I((int) reader.readFloat());
				case LONG -> new Vec4I((int) reader.readLong());
				case DOUBLE -> new Vec4I((int) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}
