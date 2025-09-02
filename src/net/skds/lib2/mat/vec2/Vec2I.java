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

@DefaultCodec(Vec2I.JCodec.class)
public record Vec2I(int xi, int yi) implements Vec2 {

	public static final Vec2I ZERO = new Vec2I(0, 0);
	public static final Vec2I XP = new Vec2I(1, 0);
	public static final Vec2I XN = new Vec2I(-1, 0);
	public static final Vec2I YP = new Vec2I(0, 1);
	public static final Vec2I YN = new Vec2I(0, -1);

	public Vec2I(int size) {
		this(size, size);
	}

	@Override
	public double x() {
		return xi;
	}

	@Override
	public double y() {
		return yi;
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
	public int floorX() {
		return xi;
	}

	@Override
	public int floorY() {
		return yi;
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
	public int roundX() {
		return xi;
	}

	@Override
	public int roundY() {
		return yi;
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
	public Vec2I getAsIntVec() {
		return this;
	}

	static final class JCodec extends AbstractCodec<Vec2> {

		public JCodec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Vec2 value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginList();
			writer.writeLong(value.xi());
			writer.writeLong(value.yi());
			writer.endList();
		}

		@Override
		public Vec2I read(UniversalReader reader) throws IOException {
			int x = 0;
			int y = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readInt();
					y = reader.readInt();
					reader.endList();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						int i = reader.readInt();
						switch (s.toLowerCase()) {
							case "x" -> x = i;
							case "y" -> y = i;
						}
					}
					reader.endObject();
				}
				case INT -> new Vec2I(reader.readInt());
				case FLOAT -> new Vec2I((int) reader.readFloat());
				case LONG -> new Vec2I((int) reader.readLong());
				case DOUBLE -> new Vec2I((int) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec2I(x, y);
		}
	}
}
