package com.ants.common.annotation.service;

import com.ants.common.enums.DataSourceType;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2017-05-18
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Source {

    String value() default "";

    DataSourceType type() default DataSourceType.NONE;
}
