package net.skds.lib2.io.codec.typed;

public interface ConfigEnumType<CT> extends ConfigType<CT> {
	@Override
	default String keyName() {
		return ((Enum<?>) this).name();
	}
}
