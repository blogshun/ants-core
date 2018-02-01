package cn.jants.common.annotation.boot;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18nConfiguration {

    String path() default "i18n";

    String use() default "";
}
