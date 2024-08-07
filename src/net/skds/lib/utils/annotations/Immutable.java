package net.skds.lib.utils.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface Immutable {
}
