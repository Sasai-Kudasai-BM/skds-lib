package net.skds.lib2.io.json.codec.typed;

import net.sdteam.libmerge.Lib1Merge;

@Lib1Merge
public interface TypedConfig {
	ConfigType<?> getConfigType();
}
