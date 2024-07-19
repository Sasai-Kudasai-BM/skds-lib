package net.skds.lib.utils.json;

public interface TypedConfig<ET extends Enum<? extends ConfigType<?>>> {
	ET getConfigType();
}
