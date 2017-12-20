package com.ants.common.annotation.plugin;


import java.lang.annotation.*;

/**
 * 缺省配置, 懒到极致
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-03
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableRedisPlugin {

    String host() default "";

    String port() default "";

    String database() default "";

    String password() default "";
}
