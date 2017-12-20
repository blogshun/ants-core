package com.ants.common.annotation.service;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-04
 */
@Inherited
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Plugin {
}
