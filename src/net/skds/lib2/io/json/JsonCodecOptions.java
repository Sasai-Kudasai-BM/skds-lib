package net.skds.lib2.io.json;

import net.skds.lib2.io.codec.CodecOptions;
import net.skds.lib2.io.codec.UniversalCodecOptionsImpl;

public interface JsonCodecOptions extends CodecOptions {

	UniversalCodecOptionsImpl setDecorationType(DecorationType decorationType);

	UniversalCodecOptionsImpl setCapabilityVersion(JsonCapabilityVersion capabilityVersion);

	UniversalCodecOptionsImpl setTabulation(String tabulation);


	DecorationType getDecorationType();

	JsonCapabilityVersion getCapabilityVersion();

	String getTabulation();

	enum DecorationType {
		FLAT,
		FANCY;
	}

	enum JsonCapabilityVersion {
		JSON,
		JSON_WITH_COMMENTS,
		JSON5
	}
}
