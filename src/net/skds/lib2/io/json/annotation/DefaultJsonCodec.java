package net.skds.lib2.io.json.annotation;

import net.skds.lib2.io.json.codec.JsonCodecFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultJsonCodec {

	Class<? extends JsonCodecFactory> value();
}
