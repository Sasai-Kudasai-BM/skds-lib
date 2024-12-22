package net.sdteam.libmerge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Lib2Merge(complete = true)
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
public @interface Lib2Merge {

	boolean complete() default false;
}
