package com.ants.common.annotation.boot;

import com.ants.common.enums.ViewType;

import java.lang.annotation.*;

/**
 * 资源配置文件注解
 *
 * @author MrShun
 * @version 1.0
 * Date 2017-04-26
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

    String encoding() default "UTF-8";
}