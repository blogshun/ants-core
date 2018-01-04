package com.ants.common.annotation.service;

import com.ants.core.ext.Interceptor;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-05-13
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aop {
    Class<? extends Interceptor>[] value();
}
