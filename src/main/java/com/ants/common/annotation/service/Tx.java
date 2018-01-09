package com.ants.common.annotation.service;


import com.ants.common.enums.TxLevel;

import java.lang.annotation.*;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-05-03
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tx {
    TxLevel value() default TxLevel.REPEATED_READ;
}
