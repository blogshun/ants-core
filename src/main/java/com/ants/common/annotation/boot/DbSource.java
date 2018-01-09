package com.ants.common.annotation.boot;

import com.ants.common.enums.DataSourceType;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbSource {

    /**
     * @return 数据源名称
     */
    String name() default "";

    /**
     * @return 默认读取自定义配置
     */
    String value() default "";

    /**
     * @return 数据库链接URL
     */
    String url() default "";

    /**
     * @return 数据库驱动, 默认MySQL驱动
     */
    String driver() default "com.mysql.jdbc.Driver";

    /**
     * @return 数据库用户名
     */
    String username() default "root";

    /**
     * @return 数据库密码
     */
    String password() default "";

    /**
     * @return 数据源类型, 默认没有数据源
     */
    DataSourceType sourceType() default DataSourceType.NONE;
}
