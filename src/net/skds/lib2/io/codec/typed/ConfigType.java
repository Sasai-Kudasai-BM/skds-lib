package net.skds.lib2.io.codec.typed;

import net.sdteam.libmerge.Lib1Merge;

@Lib1Merge
public interface ConfigType<CT> {
	Class<CT> getTypeClass();

	String keyName();
}
