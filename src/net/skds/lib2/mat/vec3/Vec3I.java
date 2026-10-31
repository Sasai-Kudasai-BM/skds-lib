package net.skds.lib2.mat.vec3;

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
@DefaultCodec(Vec3I.Codec.class)
public record Vec3I(int xi, int yi, int zi) implements Vec3, Comparable<Vec3> {

	public static final Vec3I XN = new Vec3I(-1, 0, 0);
	public static final Vec3I XP = new Vec3I(1, 0, 0);
	public static final Vec3I YN = new Vec3I(0, -1, 0);
	public static final Vec3I YP = new Vec3I(0, 1, 0);
	public static final Vec3I ZN = new Vec3I(0, 0, -1);
	public static final Vec3I ZP = new Vec3I(0, 0, 1);
	public static final Vec3I SINGLE = new Vec3I(1, 1, 1);
	public static final Vec3I ZERO = new Vec3I(0, 0, 0);

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

	public Vec3I(Vec3 vec) {
		this(vec.floorX(), vec.floorY(), vec.floorZ());
	}

	public Vec3I(int size) {
		this(size, size, size);
	}

	public Vec3I(double x, double y, double z) {
		this(FastMath.floor(x), FastMath.floor(y), FastMath.floor(z));
	}

	@Override
	public Vec3I up() {
		return addI(0, 1, 0);
	}

	@Override
	public Vec3I down() {
		return addI(0, -1, 0);
	}

	@Override
	public Vec3I left() {
		return addI(1, 0, 0);
	}

	@Override
	public Vec3I right() {
		return addI(-1, 0, 0);
	}

	@Override
	public Vec3I forward() {
		return addI(0, 0, 1);
	}

	@Override
	public Vec3I backward() {
		return addI(0, 0, -1);
	}

	@Override
	public Vec3I up(int i) {
		return addI(0, i, 0);
	}

	@Override
	public Vec3I down(int i) {
		return addI(0, -i, 0);
	}

	@Override
	public Vec3I left(int i) {
		return addI(i, 0, 0);
	}

	@Override
	public Vec3I right(int i) {
		return addI(-i, 0, 0);
	}

	@Override
	public Vec3I forward(int i) {
		return addI(0, 0, i);
	}

	@Override
	public Vec3I backward(int i) {
		return addI(0, 0, -i);
	}

	@Override
	public Vec3I up(double i) {
		return addI(0, FastMath.round(i), 0);
	}

	@Override
	public Vec3I down(double i) {
		return addI(0, FastMath.round(-i), 0);
	}

	@Override
	public Vec3I left(double i) {
		return addI(FastMath.round(i), 0, 0);
	}

	@Override
	public Vec3I right(double i) {
		return addI(FastMath.round(-i), 0, 0);
	}

	@Override
	public Vec3I forward(double i) {
		return addI(0, 0, FastMath.round(i));
	}

	@Override
	public Vec3I backward(double i) {
		return addI(0, 0, FastMath.round(-i));
	}

	public Vec3 getCenter() {
		return Vec3.of(this.xi() + 0.5, this.yi() + 0.5, this.zi() + 0.5);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof Vec3 vec && switch (vec) {
			case Vec3I(int x2, int y2, int z2) -> this.xi == x2 && this.yi == y2 && this.zi == z2;

			case Vec3D(double x2, double y2, double z2) ->
					(double) this.xi == x2 && (double) this.yi == y2 && (double) this.zi == z2;
			case Vec3F(float x2, float y2, float z2) ->
					(double) this.xi == (double) x2 && (double) this.yi == (double) y2 && (double) this.zi == (double) z2;
			case Direction dir ->
					(double) this.xi == dir.x() && (double) this.yi == dir.y() && (double) this.zi == dir.z();
		};
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3I getAsIntVec() {
		return this;
	}

	@Override
	public String getAsArrayString() {
		return "[" + this.xi() + "," + this.yi() + "," + this.zi() + "]";
	}

	@Override
	public int compareTo(Vec3 o) {
		return Vec3.compare(this, o);
	}

	static final class Codec extends AbstractCodec<Vec3> {

		public Codec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public String valueAsKeyString(Vec3 val) {
			if (val == null) {
				return null;
			}
			return val.getAsArrayString();
		}

		@Override
		public Vec3 stringKeyToValue(String key) throws IOException {
			return stringKeyToCompositeValue(key);
		}

		@Override
		public void write(Vec3 value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			writer.writeLong(value.xi());
			writer.writeLong(value.yi());
			writer.writeLong(value.zi());
			writer.endList();
		}

		@Override
		public Vec3I read(UniversalReader reader) throws IOException {
			int x = 0;
			int y = 0;
			int z = 0;

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
					reader.endList();
					yield new Vec3I(x, y, z);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x" -> x = reader.readInt();
							case "y" -> y = reader.readInt();
							case "z" -> z = reader.readInt();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec3I(x, y, z);
				}
				case INT -> new Vec3I(reader.readInt());
				case FLOAT -> new Vec3I((int) reader.readFloat());
				case LONG -> new Vec3I((int) reader.readLong());
				case DOUBLE -> new Vec3I((int) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}