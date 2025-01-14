package net.skds.lib2.io.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipSerialization {

	boolean skipNull() default true;

	boolean skipZeroSize() default true;

	String defaultString() default "";

	byte defaultByte() default 0;

	boolean defaultBoolean() default false;

	short defaultShort() default 0;

	char defaultChar() default 0;

	int defaultInt() default 0;

	long defaultLong() default 0;

	float defaultFloat() default 0;

	double defaultDouble() default 0;

	static Class<? extends Predicate<?>> BLANK_PREDICATE = BlankPredicate.class;
	Class<? extends Predicate<?>> predicate() default BlankPredicate.class;

	static class BlankPredicate implements Predicate<Object> {
		@Override
		public boolean test(Object t) {
			return false;
		}
	}

}
