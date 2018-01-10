package cn.jants.common.annotation.service;

import cn.jants.common.enums.DataSourceType;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Source {

    String value() default "";

    DataSourceType type() default DataSourceType.NONE;
}
