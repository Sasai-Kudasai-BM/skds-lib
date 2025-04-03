package net.skds.lib2.mat.pid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.exception.JsonReadException;
import net.skds.lib2.mat.vec3.Vec3D;

import java.io.IOException;
import java.lang.reflect.Type;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractPID {
	protected double p;
	protected double i;
	protected double d;

	public AbstractPID() {
		p = 1;
	}

	public abstract void reset();

	@DefaultJsonCodec(JCodec.class)
	public record PIDConfig(double p, double i, double d) {

		public PID create() {
			return new PID(p, i, d);
		}

		public Vec3PID createVec3() {
			return new Vec3PID(p, i, d);
		}
	}

	final static class JCodec extends AbstractJsonCodec<PIDConfig> {

		public JCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(PIDConfig value, JsonWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			writer.beginArray();
			writer.writeFloat(value.p());
			writer.writeFloat(value.i());
			writer.writeFloat(value.d());
			writer.endArray();
		}

		@Override
		public PIDConfig read(JsonReader reader) throws IOException {
			double p = 1;
			double i = 0;
			double d = 0;

			switch (reader.nextEntryType()) {
				case NULL -> {
					reader.skipNull();
					return null;
				}
				case BEGIN_ARRAY -> {
					reader.beginArray();
					p = reader.readDouble();
					i = reader.readDouble();
					d = reader.readDouble();
					reader.endArray();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
						String s = reader.readName();
						double v = reader.readDouble();
						switch (s.toLowerCase()) {
							case "p" -> p = v;
							case "i" -> i = v;
							case "d" -> d = v;
						}
					}
					reader.endObject();
				}
				case NUMBER -> new Vec3D(reader.readDouble());
				default ->
						throw new JsonReadException("Unsupported token in PIDConfig \"" + reader.nextEntryType() + "\"");
			}

			return new PIDConfig(p, i, d);
		}
	}
}
