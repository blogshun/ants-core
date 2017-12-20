package com.ants.common.annotation.plugin;


import java.lang.annotation.*;

/**
 * 开启Ehcache缓存
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017-12-03
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableEhcachePlugin {

    String value() default "";
}
