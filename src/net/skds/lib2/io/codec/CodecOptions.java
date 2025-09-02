package net.skds.lib2.io.codec;

public interface CodecOptions extends Cloneable {

	int getExcludeFieldModifiers();

	UniversalCodecOptions setExcludeFieldModifiers(int excludeFieldModifiers);
}
