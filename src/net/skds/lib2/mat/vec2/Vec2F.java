package net.skds.lib2.mat.vec2;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.exception.JsonReadException;

@DefaultJsonCodec(Vec2F.JCodec.class)
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

	static final class JCodec extends AbstractJsonCodec<Vec2> {

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Vec2 value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			writer.writeFloat(value.xf());
			writer.writeFloat(value.yf());
			writer.endArray();
		}

		@Override
		public Vec2F read(JsonReader reader) throws IOException {
			float x = 0;
			float y = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					x = reader.readFloat();
					y = reader.readFloat();
					reader.endArray();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
						String s = reader.readName();
						float i = reader.readFloat();
						switch (s.toLowerCase()) {
							case "x" -> x = i;
							case "y" -> y = i;
						}
					}
					reader.endObject();
				}
				case NUMBER -> new Vec2F(reader.readFloat());
				default ->
						throw new JsonReadException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec2F(x, y);
		}
	}

}
