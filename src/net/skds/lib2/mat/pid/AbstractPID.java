package net.skds.lib2.mat.pid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;

import static net.skds.lib2.io.sosison.SosisonEntryType.END_OBJECT;

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

	@DefaultCodec(JCodec.class)
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

	final static class JCodec extends AbstractCodec<PIDConfig> {

		public JCodec(Type type, CodecRegistry registry) {
			super(type, registry);
		}

		@Override
		public void write(PIDConfig value, UniversalWriter writer) throws IOException {
			if (value == null) {
				writer.writeNull();
				return;
			}
			if (value.min != DEFAULT_MIN || value.max != DEFAULT_MAX) {
				writer.beginObject();
				writer.writeDouble("p", value.p());
				writer.writeDouble("i", value.i());
				writer.writeDouble("d", value.d());
				if (value.min != DEFAULT_MIN) {
					writer.writeDouble("min", value.min());
				}
				if (value.max != DEFAULT_MAX) {
					writer.writeDouble("max", value.max());
				}
				writer.endObject();
				return;
			}
			writer.beginList();
			writer.writeDouble(value.p());
			writer.writeDouble(value.i());
			writer.writeDouble(value.d());
			writer.endList();
		}

		@Override
		public PIDConfig read(UniversalReader reader) throws IOException {
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
				case BEGIN_LIST -> {
					reader.beginList();
					p = reader.readDouble();
					i = reader.readDouble();
					d = reader.readDouble();
					reader.endList();
				}
				case BEGIN_OBJECT -> {
					reader.beginObject();
					while (reader.nextEntryType() != END_OBJECT) {
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
				default ->
						throw new ParseException("Unsupported token in PIDConfig \"" + reader.nextEntryType() + "\"");
			}

			return new PIDConfig(p, i, d, min, max);
		}
	}
}
