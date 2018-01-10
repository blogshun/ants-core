package cn.jants.common.annotation.plugin;


import java.lang.annotation.*;

/**
 * 开启Ehcache缓存
 *
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableEhcachePlugin {

    String value() default "";
}
