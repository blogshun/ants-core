package com.ants.plugin.jms;

import java.lang.annotation.*;

/**
 * ActiveMQ监听器
 *
 * @author MrShun
 * @version 1.0
 * @date 2017/12/15
 */

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JmsListener {

    String destination();
}
