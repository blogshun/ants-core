package cn.jants.common.annotation.action;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DELETE {

    String[] value() default "";

    String name() default "";

    String desc() default "";
}
