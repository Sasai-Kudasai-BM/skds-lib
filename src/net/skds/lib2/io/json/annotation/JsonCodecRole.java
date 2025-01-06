package net.skds.lib2.io.json.annotation;

import net.skds.lib2.io.CodecRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonCodecRole {

	CodecRole value();
}
