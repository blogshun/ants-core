package com.ants.common.annotation.action;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-04-26
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {

    /**
     * The URI template variable to bind to.
     */
    String value() default "";

}