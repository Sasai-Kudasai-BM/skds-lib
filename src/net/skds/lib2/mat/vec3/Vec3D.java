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
@DefaultCodec(Vec3D.Codec.class)
public record Vec3D(double x, double y, double z) implements Vec3 {

	public static final Vec3D XN = new Vec3D(-1.0D, 0.0D, 0.0D);
	public static final Vec3D XP = new Vec3D(1.0D, 0.0D, 0.0D);
	public static final Vec3D YN = new Vec3D(0.0D, -1.0D, 0.0D);
	public static final Vec3D YP = new Vec3D(0.0D, 1.0D, 0.0D);
	public static final Vec3D ZN = new Vec3D(0.0D, 0.0D, -1.0D);
	public static final Vec3D ZP = new Vec3D(0.0D, 0.0D, 1.0D);
	public static final Vec3D SINGLE = new Vec3D(1.0D, 1.0D, 1.0D);
	public static final Vec3D ZERO = new Vec3D(0.0D, 0.0D, 0.0D);

	public Vec3D(Vec3 vec) {
		this(vec.x(), vec.y(), vec.z());
	}

	public Vec3D(double size) {
		this(size, size, size);
	}

	@Override
	public Vec3D up() {
		return add(0, 1, 0);
	}

	@Override
	public Vec3D down() {
		return add(0, -1, 0);
	}

	@Override
	public Vec3D left() {
		return add(1, 0, 0);
	}

	@Override
	public Vec3D right() {
		return add(-1, 0, 0);
	}

	@Override
	public Vec3D forward() {
		return add(0, 0, 1);
	}

	@Override
	public Vec3D backward() {
		return add(0, 0, -1);
	}

	@Override
	public Vec3D up(int i) {
		return add(0, i, 0);
	}

	@Override
	public Vec3D down(int i) {
		return add(0, -i, 0);
	}

	@Override
	public Vec3D left(int i) {
		return add(i, 0, 0);
	}

	@Override
	public Vec3D right(int i) {
		return add(-i, 0, 0);
	}

	@Override
	public Vec3D forward(int i) {
		return add(0, 0, i);
	}

	@Override
	public Vec3D backward(int i) {
		return add(0, 0, -i);
	}

	@Override
	public Vec3D up(double i) {
		return add(0, i, 0);
	}

	@Override
	public Vec3D down(double i) {
		return add(0, -i, 0);
	}

	@Override
	public Vec3D left(double i) {
		return add(i, 0, 0);
	}

	@Override
	public Vec3D right(double i) {
		return add(-i, 0, 0);
	}

	@Override
	public Vec3D forward(double i) {
		return add(0, 0, i);
	}

	@Override
	public Vec3D backward(double i) {
		return add(0, 0, -i);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof Vec3 vec && switch (vec) {
			case Vec3D(double x2, double y2, double z2) -> this.x == x2 && this.y == y2 && this.z == z2;
			case Vec3F(float x2, float y2, float z2) -> this.x == x2 && this.y == y2 && this.z == z2;
			case Vec3I(int x2, int y2, int z2) -> this.x == x2 && this.y == y2 && this.z == z2;
			case Direction dir -> this.x == dir.x() && this.y == dir.y() && this.z == dir.z();
		};
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3D getAsDoubleVec() {
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
			writer.writeDouble(value.x());
			writer.writeDouble(value.y());
			writer.writeDouble(value.z());
			writer.endList();
		}

		@Override
		public Vec3D read(UniversalReader reader) throws IOException {
			double x = 0;
			double y = 0;
			double z = 0;

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
					reader.endList();
					yield new Vec3D(x, y, z);
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						switch (s.toLowerCase()) {
							case "x" -> x = reader.readDouble();
							case "y" -> y = reader.readDouble();
							case "z" -> z = reader.readDouble();
							default -> reader.skipValue();
						}
					}
					reader.endObject();
					yield new Vec3D(x, y, z);
				}
				case INT -> new Vec3D(reader.readInt());
				case FLOAT -> new Vec3D(reader.readFloat());
				case LONG -> new Vec3D(reader.readLong());
				case DOUBLE -> new Vec3D(reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			};
		}
	}
}