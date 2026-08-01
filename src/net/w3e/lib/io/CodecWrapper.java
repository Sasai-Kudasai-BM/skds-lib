package net.w3e.lib.io;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;

public record CodecWrapper<O, R>(Class<O> originalClass, Class<R> replacedClass, Converter<O, R> originalConverter, Converter<R, O> replacedConverter) {

	@FunctionalInterface
	public interface Converter<A, B> {
		B convert(A object) throws Exception;
	}

	@SuppressWarnings("unchecked")
	public <T> T convert(Object object) throws Exception {
		if (object == null) {
			return null;
		}
		if (originalClass.isAssignableFrom(object.getClass())) {
			return (T) this.originalConverter.convert((O) object);
		}
		if (replacedClass.isAssignableFrom(object.getClass())) {
			return (T) this.replacedConverter.convert((R) object);
		}
		return null;
	}

	public static <O, R> void registerToSosison(Class<O> originalClass, Class<R> replacedClass, Converter<O, R> originalConverter, Converter<R, O> replacedConverter) {
		registerToSosison(new CodecWrapper<>(originalClass, replacedClass, originalConverter, replacedConverter));
	}

	public static void registerToSosison(CodecWrapper<?, ?> converter) {
		SosisonUtils.addFactory(converter.replacedClass, (type, registry) -> new ConverterCodec<>(converter, type, registry));
	}

	private static class ConverterCodec<O, R> extends AbstractCodec<R> {

		private final CodecWrapper<O, R> converter;
		private final UniversalCodec<O> codec;

		public ConverterCodec(CodecWrapper<O, R> converter, Type type, CodecRegistry registry) {
			super(type, registry);
			this.converter = converter;
			this.codec = registry.getCodecIndirect(converter.originalClass);
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
