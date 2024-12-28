package net.skds.lib2.io.json.annotation;

import net.skds.lib2.io.json.codec.JsonCodecFactory;

public @interface DefaultJsonCodec {

	Class<? extends JsonCodecFactory> value();
}
