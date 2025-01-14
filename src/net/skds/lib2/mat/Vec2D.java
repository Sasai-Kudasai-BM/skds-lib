package net.skds.lib2.mat;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.exception.JsonReadException;

@DefaultJsonCodec(Vec2D.JCodec.class)
public record Vec2D(double x, double y) implements Vec2 {
	public static final Vec2D ZERO = new Vec2D(0.0D, 0.0D);

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
			writer.writeFloat(value.x());
			writer.writeFloat(value.y());
			writer.endArray();
		}

		@Override
		public Vec2D read(JsonReader reader) throws IOException {
			double x = 0;
			double y = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					x = reader.readDouble();
					y = reader.readDouble();
					reader.endArray();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
						String s = reader.readName();
						double i = reader.readDouble();
						switch (s.toLowerCase()) {
							case "x" -> x = i;
							case "y" -> y = i;
						}
					}
					reader.endObject();
				}
				case NUMBER -> new Vec2D(reader.readDouble());
				default ->
						throw new JsonReadException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec2D(x, y);
		}
	}
}
