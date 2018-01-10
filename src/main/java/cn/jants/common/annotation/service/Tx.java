package cn.jants.common.annotation.service;


import cn.jants.common.enums.TxLevel;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tx {
    TxLevel value() default TxLevel.REPEATED_READ;
}
