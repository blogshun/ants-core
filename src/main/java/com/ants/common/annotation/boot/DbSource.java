package com.ants.common.annotation.boot;

import com.ants.common.enums.DataSourceType;

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
public @interface DbSource {

    /**
     * 数据源名称
     *
     * @return
     */
    String name() default "";

    /**
     * 默认读取自定义配置
     */
    String value() default "";

    /**
     * 数据库链接URL
     */
    String url() default "";

    /**
     * 数据库驱动, 默认MySQL驱动
     */
    String driver() default "com.mysql.jdbc.Driver";

    /**
     * 数据库用户名
     */
    String username() default "root";

    /**
     * 数据库密码
     */
    String password() default "";

    /**
     * 数据源类型, 默认没有数据源
     */
    DataSourceType sourceType() default DataSourceType.NONE;
}
