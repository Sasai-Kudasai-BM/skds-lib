package net.skds.lib2.natives.wrapper.annotation;

import net.skds.lib2.utils.SKDSUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface LibraryName {

	String value();

	SKDSUtils.OSType os();
}
