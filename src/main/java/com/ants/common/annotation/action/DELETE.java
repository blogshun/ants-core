package com.ants.common.annotation.action;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-04-26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DELETE {

    String[] value() default "";

    String name() default "";

    String desc() default "";
}
