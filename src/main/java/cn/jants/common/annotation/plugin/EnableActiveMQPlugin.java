package cn.jants.common.annotation.plugin;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableActiveMQPlugin {

    String brokerUrl() default "";

    String username() default "";

    String password() default "";
}
