package com.ants.common.annotation.action;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-04-26
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";

    String desc() default "";
}