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

@DefaultCodec(Vec2F.JCodec.class)
public record Vec2F(float xf, float yf) implements Vec2 {
	public static final Vec2F ZERO = new Vec2F(0.0F, 0.0F);

	public Vec2F(float size) {
		this(size, size);
	}

	@Override
	public double x() {
		return xf;
	}

	@Override
	public double y() {
		return yf;
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
	public Vec2F getAsFloatVec() {
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
			writer.writeFloat(value.xf());
			writer.writeFloat(value.yf());
			writer.endList();
		}

		@Override
		public Vec2F read(UniversalReader reader) throws IOException {
			float x = 0;
			float y = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_LIST -> {
					reader.beginList();
					x = reader.readFloat();
					y = reader.readFloat();
					reader.endList();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
						String s = reader.readName();
						float i = reader.readFloat();
						switch (s.toLowerCase()) {
							case "x" -> x = i;
							case "y" -> y = i;
						}
					}
					reader.endObject();
				}
				case INT -> new Vec2F(reader.readInt());
				case FLOAT -> new Vec2F(reader.readFloat());
				case LONG -> new Vec2F(reader.readLong());
				case DOUBLE -> new Vec2F((float) reader.readDouble());
				default -> throw new ParseException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec2F(x, y);
		}
	}

}
