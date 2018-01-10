package cn.jants.plugin.orm;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name();
}

