package cn.jants.common.annotation.plugin;


import java.lang.annotation.*;

/**
 * 缺省配置, 懒到极致
 *
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMongoDbPlugin {

    String uri() default "";
}
