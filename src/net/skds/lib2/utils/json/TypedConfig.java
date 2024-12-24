package net.skds.lib2.utils.json;

import net.sdteam.libmerge.Lib1Merge;

@Lib1Merge
public interface TypedConfig {
	ConfigType<?> getConfigType();
}
