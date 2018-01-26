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
public @interface EnableSQLMapPlugin {

    /**
     * 配置文件
     * @return
     */
    String value() default "";

    /**
     * 是否驼峰, 默认false
     */
    boolean hump() default false;
}
