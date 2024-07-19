package net.skds.lib.utils.json;

public interface TypedConfig {
	<E extends Enum<? extends ConfigType<?>>> E getConfigType();
}
