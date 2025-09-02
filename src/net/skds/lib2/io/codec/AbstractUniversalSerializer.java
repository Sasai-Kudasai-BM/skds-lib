package net.skds.lib2.io.codec;

public abstract class AbstractUniversalSerializer<T> implements UniversalSerializer<T> {
	final UniversalCodecRegistry registry;

	protected AbstractUniversalSerializer(UniversalCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public UniversalCodecRegistry getRegistry() {
		return registry;
	}
}
