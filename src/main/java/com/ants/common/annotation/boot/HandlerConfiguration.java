package com.ants.common.annotation.boot;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-03
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HandlerConfiguration {
}
