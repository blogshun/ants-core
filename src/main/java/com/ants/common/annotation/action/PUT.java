package com.ants.common.annotation.action;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-04-26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PUT {

    String[] value() default "";

    String name() default "";

    String desc() default "";
}
