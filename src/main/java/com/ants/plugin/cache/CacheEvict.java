package com.ants.plugin.cache;


import java.lang.annotation.*;

/**
 * 清除缓存
 *
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {

    String value() default "";

    String key() default "";
}
