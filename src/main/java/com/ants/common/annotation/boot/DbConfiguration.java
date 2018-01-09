package com.ants.common.annotation.boot;

import java.lang.annotation.*;

/**
 * 缺省配置, 懒到极致
 *
 * @author MrShun
 * @version 1.0
 * @date 2017-12-03
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DbConfiguration {

    DbSource[] dbs() default {};

}
