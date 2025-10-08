package net.skds.lib2.io.codec;

import net.skds.lib2.io.json.JsonCodecOptions;
import net.skds.lib2.io.sosison.SosisonCodecOptions;

public interface UniversalCodecOptions extends JsonCodecOptions, SosisonCodecOptions {

	UniversalCodecOptions clone();
}