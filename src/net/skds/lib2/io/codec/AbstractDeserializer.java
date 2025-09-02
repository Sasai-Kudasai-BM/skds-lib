package net.skds.lib2.io.codec;

public abstract class AbstractDeserializer<T> implements UniversalDeserializer<T> {
	final CodecRegistry registry;

	protected AbstractDeserializer(CodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public CodecRegistry getRegistry() {
		return registry;
	}
}
