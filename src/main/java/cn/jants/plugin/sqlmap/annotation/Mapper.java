package cn.jants.plugin.sqlmap.annotation;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapper {

    String value() default "";
}
