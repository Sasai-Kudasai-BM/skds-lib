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

@DefaultJsonCodec(Vec2I.JCodec.class)
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
			writer.writeInt(value.xi());
			writer.writeInt(value.yi());
			writer.endArray();
		}

		@Override
		public Vec2I read(JsonReader reader) throws IOException {
			int x = 0;
			int y = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					x = reader.readInt();
					y = reader.readInt();
					reader.endArray();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
						String s = reader.readName();
						int i = reader.readInt();
						switch (s.toLowerCase()) {
							case "x" -> x = i;
							case "y" -> y = i;
						}
					}
					reader.endObject();
				}
				case NUMBER -> new Vec2I(reader.readInt());
				default ->
						throw new JsonReadException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec2I(x, y);
		}
	}
}
