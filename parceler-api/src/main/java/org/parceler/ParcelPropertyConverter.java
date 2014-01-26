package org.parceler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author John Ericksen
 */
@Target({FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
public @interface ParcelPropertyConverter {
    Class<? extends ParcelConverter> value();
}
