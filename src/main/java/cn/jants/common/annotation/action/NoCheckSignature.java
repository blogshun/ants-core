package cn.jants.common.annotation.action;

import java.lang.annotation.*;

/**
 * 不做签名校验注解
 *
 * @author MrShun
 * @version 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoCheckSignature {
}
