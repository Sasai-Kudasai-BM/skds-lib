package net.skds.lib2.io.codec;

public abstract class AbstractDeserializer<T> implements UniversalDeserializer<T> {
	final UniversalCodecRegistry registry;

	protected AbstractDeserializer(UniversalCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public UniversalCodecRegistry getRegistry() {
		return registry;
	}
}
