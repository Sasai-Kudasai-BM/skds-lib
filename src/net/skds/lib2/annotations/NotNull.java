package net.skds.lib2.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.SOURCE)
public @interface NotNull {
}
