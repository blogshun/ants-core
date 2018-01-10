package cn.jants.common.annotation.service;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Application {

    String[] scanPackages() default "";
}
