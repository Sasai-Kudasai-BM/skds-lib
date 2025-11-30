package net.skds.lib2.mat.vec2;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.lang.reflect.Type;

@DefaultCodec(Vec2D.Codec.class)
public record Vec2D(double x, double y) implements Vec2 {

	public static final Vec2D XP = new Vec2D(1, 0);
	public static final Vec2D XN = new Vec2D(-1, 0);
	public static final Vec2D YP = new Vec2D(0, 1);
	public static final Vec2D YN = new Vec2D(0, -1);

	public static final Vec2D ZERO = new Vec2D(0.0D, 0.0D);
	public static final Vec2D SINGLE = new Vec2D(1, 1);

	public Vec2D(double size) {
		this(size, size);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Vec2 vec) {
			return Vec2.equals(this, vec);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Vec2.hashCode(this);
	}

	@Override
	public Vec2D getAsDoubleVec() {
		return this;
	}

	static final class Codec extends AbstractCodec<Vec2> {

		public Codec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public String valueAsKeyString(Vec2 val) {
			if (val == null) {
				return null;
			}
			return "[" + val.x() + "," + val.y() + "]";
		}

		@Override
		public Vec2 stringKeyToValue(String key) throws IOException {
			return stringKeyToCompositeValue(key);
		}

		@Override
		public void write(Vec2 value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			writer.writeDouble(value.x());
			writer.writeDouble(value.y());
			writer.endList();
		}

		@Override
		public Vec2D read(UniversalReader reader) throws IOException {
			double x = 0;
			double y = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readDouble();
					y = reader.readDouble();
					reader.endList();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						double i = reader.readDouble();
						switch (s.toLowerCase()) {
							case "x" -> x = i;
							case "y" -> y = i;
						}
					}
					reader.endObject();
				}
				case INT -> new Vec2D(reader.readInt());
				case FLOAT -> new Vec2D(reader.readFloat());
				case LONG -> new Vec2D(reader.readLong());
				case DOUBLE -> new Vec2D(reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec2D(x, y);
		}
	}
}
