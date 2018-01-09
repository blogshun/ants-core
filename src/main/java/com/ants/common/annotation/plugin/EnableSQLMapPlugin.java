package com.ants.common.annotation.plugin;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-12-05
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSQLMapPlugin {

    /**
     * 配置文件
     * @return
     */
    String value() default "";
}
