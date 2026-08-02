package net.w3e.lib;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;

public record ObjectWrapper<O, R>(Class<O> rawFormat, Class<R> targetFormat, Converter<O, R> rawConverter,
								  Converter<R, O> targetConverter) {

	@FunctionalInterface
	public interface Converter<A, B> {
		B convert(A object) throws Exception;
	}

	public R convertOR(O object) {
		if (object == null) {
			return null;
		}
		try {
			return this.rawConverter.convert(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public O convertRO(R object) {
		if (object == null) {
			return null;
		}
		try {
			return this.targetConverter.convert(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T convert(Object object) {
		if (object == null) {
			return null;
		}
		if (rawFormat.isAssignableFrom(object.getClass())) {
			try {
				return (T) this.rawConverter.convert((O) object);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (targetFormat.isAssignableFrom(object.getClass())) {
			try {
				return (T) this.targetConverter.convert((R) object);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static <O, R> void registerToSosison(Class<O> originalClass, Class<R> replacedClass, Converter<O, R> originalConverter, Converter<R, O> replacedConverter) {
		registerToSosison(new ObjectWrapper<>(originalClass, replacedClass, originalConverter, replacedConverter));
	}

	public static void registerToSosison(ObjectWrapper<?, ?> converter) {
		SosisonUtils.addFactory(converter.targetFormat, (type, registry) -> new ConverterCodec<>(converter, type, registry));
	}

	private static final ObjectWrapper.Converter<?, ?> UNIMPLEMENTED_METHOD_WRITE = _ -> {
		throw new UnsupportedOperationException("Unimplemented method 'write'");
	};

	@SuppressWarnings("unchecked")
	public static <O, R> ObjectWrapper.Converter<R, O> throwUnimplementedMethodWrite() {
		return (ObjectWrapper.Converter<R, O>) UNIMPLEMENTED_METHOD_WRITE;
	}

	private static class ConverterCodec<O, R> extends AbstractCodec<R> {

		private final ObjectWrapper<O, R> converter;
		private final UniversalCodec<O> codec;

		public ConverterCodec(ObjectWrapper<O, R> converter, Type type, CodecRegistry registry) {
			super(type, registry);
			this.converter = converter;
			this.codec = registry.getCodecIndirect(converter.rawFormat);
		}

		@Override
		public R read(UniversalReader reader) throws IOException {
			try {
				return this.converter.convert(this.codec.read(reader));
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}

		@Override
		public void write(R value, UniversalWriter writer) throws IOException {
			try {
				this.codec.write(this.converter.convert(value), writer);
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}
	}
}
