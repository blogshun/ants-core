package cn.jants.common.annotation.action;

import cn.jants.common.enums.Regex;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String regex() default "";

    String msg() default "";

    Regex type() default Regex.NONE;
}
