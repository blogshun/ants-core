package cn.jants.common.annotation.service;


import cn.jants.core.ext.Interceptor;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Uop {
    Class<? extends Interceptor>[] value();
}
