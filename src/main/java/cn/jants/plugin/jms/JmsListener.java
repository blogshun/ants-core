package cn.jants.plugin.jms;

import java.lang.annotation.*;

/**
 * ActiveMQ监听器
 *
 * @author MrShun
 * @version 1.0
 */

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JmsListener {

    String destination();
}
