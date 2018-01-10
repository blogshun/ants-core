package cn.jants.common.annotation.boot;

import java.lang.annotation.*;

/**
 * 资源配置文件注解
 *
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertyConfiguration {

    String[] value() default {};

    String[] suffix() default "";

    String[] resources() default "";

    String page404() default "";

    String page500() default "";

    boolean debug() default false;

    String secretKey() default "";

    String encoding() default "UTF-8";
}