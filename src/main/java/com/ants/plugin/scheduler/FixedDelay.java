package com.ants.plugin.scheduler;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FixedDelay {

    int initialDelay() default 1000;

    /**
     * 单位是毫秒
     */
    int delay() default 1000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
