package cn.jants.plugin.sqlmap.annotation;

import cn.jants.plugin.sqlmap.enums.OptionType;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sql {

    String value();

    OptionType type();

    String resultType() default "";
}
