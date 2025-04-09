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

	protected static final double DEFAULT_MIN = -Double.MAX_VALUE;
	protected static final double DEFAULT_MAX = Double.MAX_VALUE;

	protected double p;
	protected double i;
	protected double d;

	protected double iLimMin = DEFAULT_MIN;
	protected double iLimMax = DEFAULT_MAX;

	public AbstractPID() {
		p = 1;
	}

	public AbstractPID(double p, double i, double d) {
		this.p = p;
		this.i = i;
		this.d = d;
	}

	public abstract void reset();

	@DefaultJsonCodec(JCodec.class)
	public record PIDConfig(double p, double i, double d, double min, double max) {

		public PIDConfig(double p, double i, double d) {
			this(p, i, d, DEFAULT_MIN, DEFAULT_MAX);
		}

		public PID create() {
			return new PID(p, i, d, min, max);
		}

		public Vec3PID createVec3() {
			return new Vec3PID(p, i, d, min, max);
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
			if (value.min != DEFAULT_MIN || value.max != DEFAULT_MAX) {
				writer.beginObject();
				writer.writeFloat("p", value.p());
				writer.writeFloat("i", value.i());
				writer.writeFloat("d", value.d());
				if (value.min != DEFAULT_MIN) {
					writer.writeFloat("min", value.min());
				}
				if (value.max != DEFAULT_MAX) {
					writer.writeFloat("max", value.max());
				}
				writer.endObject();
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

			double min = DEFAULT_MIN;
			double max = DEFAULT_MAX;

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
							case "mim" -> min = v;
							case "max" -> max = v;
						}
					}
					reader.endObject();
				}
				case NUMBER -> new Vec3D(reader.readDouble());
				default ->
						throw new JsonReadException("Unsupported token in PIDConfig \"" + reader.nextEntryType() + "\"");
			}

			return new PIDConfig(p, i, d, min, max);
		}
	}
}
