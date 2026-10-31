package net.skds.lib2.mat.vec3;

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
@DefaultCodec(Vec3F.Codec.class)
public record Vec3F(float xf, float yf, float zf) implements Vec3 {

	public static final Vec3F XN = new Vec3F(-1.0F, 0.0F, 0.0F);
	public static final Vec3F XP = new Vec3F(1.0F, 0.0F, 0.0F);
	public static final Vec3F YN = new Vec3F(0.0F, -1.0F, 0.0F);
	public static final Vec3F YP = new Vec3F(0.0F, 1.0F, 0.0F);
	public static final Vec3F ZN = new Vec3F(0.0F, 0.0F, -1.0F);
	public static final Vec3F ZP = new Vec3F(0.0F, 0.0F, 1.0F);
	public static final Vec3F SINGLE = new Vec3F(1.0F, 1.0F, 1.0F);
	public static final Vec3F ZERO = new Vec3F(0.0F, 0.0F, 0.0F);

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

	public Vec3F(Vec3 vec) {
		this(vec.xf(), vec.yf(), vec.zf());
	}

	public Vec3F(float size) {
		this(size, size, size);
	}

	@Override
	public Vec3F up() {
		return addF(0, 1, 0);
	}

	@Override
	public Vec3F down() {
		return addF(0, -1, 0);
	}

	@Override
	public Vec3F left() {
		return addF(1, 0, 0);
	}

	@Override
	public Vec3F right() {
		return addF(-1, 0, 0);
	}

	@Override
	public Vec3F forward() {
		return addF(0, 0, 1);
	}

	@Override
	public Vec3F backward() {
		return addF(0, 0, -1);
	}

	@Override
	public Vec3F up(int i) {
		return addF(0, i, 0);
	}

	@Override
	public Vec3F down(int i) {
		return addF(0, -i, 0);
	}

	@Override
	public Vec3F left(int i) {
		return addF(i, 0, 0);
	}

	@Override
	public Vec3F right(int i) {
		return addF(-i, 0, 0);
	}

	@Override
	public Vec3F forward(int i) {
		return addF(0, 0, i);
	}

	@Override
	public Vec3F backward(int i) {
		return addF(0, 0, -i);
	}

	@Override
	public Vec3F up(double i) {
		return addF(0, (float) i, 0);
	}

	@Override
	public Vec3F down(double i) {
		return addF(0, (float) -i, 0);
	}

	@Override
	public Vec3F left(double i) {
		return addF((float) i, 0, 0);
	}

	@Override
	public Vec3F right(double i) {
		return addF((float) -i, 0, 0);
	}

	@Override
	public Vec3F forward(double i) {
		return addF(0, 0, (float) i);
	}

	@Override
	public Vec3F backward(double i) {
		return addF(0, 0, (float) -i);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof Vec3 vec && switch (vec) {
			case Vec3F(float x2, float y2, float z2) -> this.xf == x2 && this.yf == y2 && this.zf == z2;

			case Vec3D(double x2, double y2, double z2) ->
					(double) this.xf == x2 && (double) this.yf == y2 && (double) this.zf == z2;
			case Vec3I(int x2, int y2, int z2) ->
					(double) this.xf == (double) x2 && (double) this.yf == (double) y2 && (double) this.zf == (double) z2;
			case Direction dir ->
					(double) this.xf == dir.x() && (double) this.yf == dir.y() && (double) this.zf == dir.z();
		};
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3F getAsFloatVec() {
		return this;
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
			return "[" + val.xf() + "," + val.yf() + "," + val.zf() + "]";
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
			writer.writeFloat(value.xf());
			writer.writeFloat(value.yf());
			writer.writeFloat(value.zf());
			writer.endList();
		}

		@Override
		public Vec3F read(UniversalReader reader) throws IOException {
			float x = 0;
			float y = 0;
			float z = 0;

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
					reader.endList();
					yield new Vec3F(x, y, z);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x" -> x = reader.readFloat();
							case "y" -> y = reader.readFloat();
							case "z" -> z = reader.readFloat();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec3F(x, y, z);
				}
				case INT -> new Vec3F(reader.readInt());
				case FLOAT -> new Vec3F(reader.readFloat());
				case LONG -> new Vec3F(reader.readLong());
				case DOUBLE -> new Vec3F((float) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}