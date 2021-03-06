package cn.jants.plugin.cache;


import java.lang.annotation.*;

/**
 * 主要是给Ehcache使用
 *
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    String value() default "";

    String key() default "";

    /**
     * 默认100个小时
     * @return
     */
    int seconds() default 60000;
}
