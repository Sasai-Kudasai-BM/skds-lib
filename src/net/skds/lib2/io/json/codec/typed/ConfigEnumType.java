package net.skds.lib2.io.json.codec.typed;

public interface ConfigEnumType<CT> extends ConfigType<CT> {
	@Override
	default String keyName() {
		return ((Enum<?>)this).name();
	}
}
