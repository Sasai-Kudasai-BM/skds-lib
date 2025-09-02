package net.skds.lib2.io.codec;

public abstract class AbstractUniversalSerializer<T> implements UniversalSerializer<T> {
	final CodecRegistry registry;

	protected AbstractUniversalSerializer(CodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public CodecRegistry getRegistry() {
		return registry;
	}
}
