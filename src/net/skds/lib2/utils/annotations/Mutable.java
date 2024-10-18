package net.skds.lib2.utils.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Mutable {

	MutateTarget value() default MutateTarget.ARGUMENT;

	enum MutateTarget {
		ARGUMENT,
		THIS;
	}
}
