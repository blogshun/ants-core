package com.ants.plugin.orm;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-09-06
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {

}
