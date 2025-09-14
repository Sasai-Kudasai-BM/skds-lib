package net.skds.lib2.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.SOURCE)
public @interface Nullable {
}
