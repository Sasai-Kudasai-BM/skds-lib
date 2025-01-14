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

@SuppressWarnings("unused")
@DefaultJsonCodec(Vec3D.JCodec.class)
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
	public Vec3D getAsDoubleVec() {
		return this;
	}

	static final class JCodec extends AbstractJsonCodec<Vec3> {

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(Vec3 value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			writer.writeFloat(value.x());
			writer.writeFloat(value.y());
			writer.writeFloat(value.z());
			writer.endArray();
		}

		@Override
		public Vec3D read(JsonReader reader) throws IOException {
			double x = 0;
			double y = 0;
			double z = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					x = reader.readDouble();
					y = reader.readDouble();
					z = reader.readDouble();
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
							case "z" -> z = i;
						}
					}
					reader.endObject();
				}
				case NUMBER -> new Vec3D(reader.readDouble());
				default ->
						throw new JsonReadException("Unsupported token in vector \"" + reader.nextEntryType() + "\"");
			}

			return new Vec3D(x, y, z);
		}
	}
}