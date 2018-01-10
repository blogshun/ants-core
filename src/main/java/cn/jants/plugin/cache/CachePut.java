package cn.jants.plugin.cache;


import java.lang.annotation.*;

/**
 * 更新缓存
 *
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CachePut {

    /**
     * 缓存名称
     *
     * @return
     */
    String value() default "";

    String key() default "";

    /**
     * 默认100个小时
     * @return
     */
    int seconds() default 60000;
}
