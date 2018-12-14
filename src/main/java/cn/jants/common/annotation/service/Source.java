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

    /**
     * 数据库链接对象
     *
     * @return
     */
    String value() default "";

    /**
     * 数据源类型
     *
     * @return
     */
    DataSourceType type() default DataSourceType.NONE;
}
