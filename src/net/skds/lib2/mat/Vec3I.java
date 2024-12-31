package net.skds.lib2.mat;


import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReadException;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecFactory;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

import java.io.IOException;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
@DefaultJsonCodec(Vec3I.JCodecFactory.class)
public record Vec3I(int xi, int yi, int zi) implements Vec3 {

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Vec3 vec) {
			return Vec3.equals(this, vec);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Vec3.hashCode(this);
	}

	@Override
	public Vec3I getAsIntVec() {
		return this;
	}

	public static final class JCodecFactory implements JsonCodecFactory {
		@Override
		public JsonCodec<?> createCodec(Type type, JsonCodecRegistry registry) {
			return new JCodec(registry);
		}
	}

	private static final class JCodec extends JsonCodec<Vec3I> {

		public JCodec(JsonCodecRegistry registry) {
			super(registry);
		}

		@Override
		public void write(Vec3I value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			writer.writeInt(value.xi);
			writer.writeInt(value.yi);
			writer.writeInt(value.zi);
			writer.endArray();
		}

		@Override
		public Vec3I read(JsonReader reader) throws IOException {
			int x = 0;
			int y = 0;
			int z = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					x = reader.readInt();
					y = reader.readInt();
					z = reader.readInt();
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
							case "z" -> z = i;
						}
					}
					reader.endObject();
				}
				default ->
						throw new JsonReadException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec3I(x, y, z);
		}
	}
}