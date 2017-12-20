package com.ants.plugin.scheduler;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017/12/15
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FixedDelay {

    int initialDelay() default 1000;

    int delay() default 1000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
