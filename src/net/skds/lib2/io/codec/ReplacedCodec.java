package net.skds.lib2.io.codec;

import java.io.IOException;
import java.lang.reflect.Type;

public class ReplacedCodec extends AbstractCodec<Object> {

	private final UniversalCodec<Object> codec;

	public ReplacedCodec(Type originalType, Type newType, CodecRegistry registry) {
		super(originalType, registry);
		this.codec = this.registry.getCodecIndirect(newType);
	}

	@Override
	public final void write(Object value, UniversalWriter writer) throws IOException {
		codec.write(value, writer);
	}

	@Override
	public final Object read(UniversalReader reader) throws IOException {
		return this.codec.read(reader);
	}
}
