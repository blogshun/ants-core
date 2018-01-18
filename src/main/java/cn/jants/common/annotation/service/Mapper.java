package cn.jants.common.annotation.service;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @Date 2018-01-17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapper {

    String value() default "";
}
