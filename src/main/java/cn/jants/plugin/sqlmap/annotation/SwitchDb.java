package cn.jants.plugin.sqlmap.annotation;

import cn.jants.core.module.DbManager;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwitchDb {

    String value() default "";
}
