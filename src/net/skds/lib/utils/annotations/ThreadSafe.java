package net.skds.lib.utils.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface ThreadSafe {

	Safety value() default Safety.READ_ONLY;

	enum Safety {
		READ_ONLY,
		READ_WRITE
	}
}
